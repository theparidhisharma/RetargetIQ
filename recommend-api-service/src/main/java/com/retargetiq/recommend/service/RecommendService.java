package com.retargetiq.recommend.service;

import com.retargetiq.recommend.model.CandidateItem;
import com.retargetiq.recommend.model.RecommendationResponse;
import com.retargetiq.recommend.model.RankedItem;
import com.retargetiq.recommend.model.UserFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.*;

@Service
public class RecommendService {
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(RecommendService.class);

    @Value("${feature.store.url:http://feature-store-service:3004}")
    private String featureStoreUrl;

    @Value("${retrieval.url:http://retrieval-service:3005}")
    private String retrievalUrl;

    @Value("${ranking.url:http://ranking-service:3006}")
    private String rankingUrl;

    @Value("${rl.offer.url:http://rl-offer-service:3007}")
    private String rlOfferUrl;

    public RecommendService() {
        this.restTemplate = buildRestTemplate();
    }

    private RestTemplate buildRestTemplate() {
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(2000);
        rf.setReadTimeout(5000);
        return new RestTemplate(rf);
    }

    public RecommendationResponse getRecommendations(String userId, int k) {
        try {
            // 1) Get user features
            UserFeatures userFeatures = fetchUserFeatures(userId);
            if (userFeatures == null) {
                logger.warn("No user features found for {}", userId);
                userFeatures = new UserFeatures(); // empty fallback
            }

            // 2) Retrieve candidate items
            CandidateItem[] candidates = fetchCandidates(userId, 500);
            if (candidates == null || candidates.length == 0) {
                logger.warn("No candidates returned for {}", userId);
                return new RecommendationResponse(userId, Collections.emptyList(), null);
            }

            // 3) Rank candidates (call ranking service)
            RankedItem[] ranked = rankCandidates(userFeatures, candidates);
            List<RankedItem> topK;
            if (ranked == null) {
                // fallback: take first k candidates
                topK = fallbackTopK(candidates, k);
            } else {
                topK = getTopKFromRanked(ranked, k);
            }

            // 4) Ask RL/Bandit service for offer decision
            Map<String, Object> offer = fetchOffer(userFeatures, topK);

            // 5) Build response
            RecommendationResponse resp = new RecommendationResponse(userId, topK, offer);
            return resp;

        } catch (Exception e) {
            logger.error("Error building recommendation for {} : {}", userId, e.getMessage(), e);
            return null;
        }
    }

    private UserFeatures fetchUserFeatures(String userId) {
        try {
            String url = String.format("%s/features/%s", featureStoreUrl, userId);
            ResponseEntity<UserFeatures> r = restTemplate.getForEntity(url, UserFeatures.class);
            if (r.getStatusCode().is2xxSuccessful()) return r.getBody();
        } catch (RestClientException e) {
            logger.warn("fetchUserFeatures failed: {}", e.getMessage());
        }
        return null;
    }

    private CandidateItem[] fetchCandidates(String userId, int limit) {
        try {
            String url = String.format("%s/retrieve?userId=%s&limit=%d", retrievalUrl, userId, limit);
            ResponseEntity<CandidateItem[]> r = restTemplate.getForEntity(url, CandidateItem[].class);
            if (r.getStatusCode().is2xxSuccessful()) return r.getBody();
        } catch (RestClientException e) {
            logger.warn("fetchCandidates failed: {}", e.getMessage());
        }
        return null;
    }

    private RankedItem[] rankCandidates(UserFeatures userFeatures, CandidateItem[] candidates) {
        try {
            String url = String.format("%s/rank", rankingUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> payload = new HashMap<>();
            payload.put("userFeatures", userFeatures);
            payload.put("candidates", candidates);
            HttpEntity<Map<String, Object>> req = new HttpEntity<>(payload, headers);
            ResponseEntity<RankedItem[]> r = restTemplate.postForEntity(url, req, RankedItem[].class);
            if (r.getStatusCode().is2xxSuccessful()) return r.getBody();
        } catch (RestClientException e) {
            logger.warn("rankCandidates failed: {}", e.getMessage());
        }
        return null;
    }

    private Map<String, Object> fetchOffer(UserFeatures userFeatures, List<RankedItem> topK) {
        try {
            String url = String.format("%s/offer", rlOfferUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = new HashMap<>();
            payload.put("userFeatures", userFeatures);
            payload.put("rankedItems", topK);

            HttpEntity<Map<String, Object>> req = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> r = restTemplate.postForEntity(url, req, Map.class);

            if (r.getStatusCode().is2xxSuccessful()) return r.getBody();

        } catch (RestClientException e) {
            logger.warn("fetchOffer failed: {}", e.getMessage());
        }
        return null;
    }

    // helper fallback methods
    private List<RankedItem> fallbackTopK(CandidateItem[] candidates, int k) {
        List<RankedItem> list = new ArrayList<>();
        for (int i = 0; i < Math.min(k, candidates.length); i++) {
            CandidateItem c = candidates[i];
            RankedItem r = new RankedItem(c.getItemId(), c.getScore(), c.getMetadata());
            list.add(r);
        }
        return list;
    }

    private List<RankedItem> getTopKFromRanked(RankedItem[] ranked, int k) {
        List<RankedItem> list = Arrays.asList(ranked);
        list.sort((a,b) -> Double.compare(b.getScore(), a.getScore()));
        return list.subList(0, Math.min(k, list.size()));
    }
}
