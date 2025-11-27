package com.retargetiq.retrieval.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class RetrievalService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${feature.store.url:http://feature-store-service:3004}")
    private String featureStoreUrl;

    // Simple product universe for demo; replace with analytics-backed product list later
    private final List<Integer> productUniverse = IntStream.rangeClosed(1, 200).boxed().collect(Collectors.toList());

    public List<Integer> getCandidates(String userId) {
        try {
            String url = String.format("%s/features/%s", featureStoreUrl, userId);
            ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> features = resp.getBody();
            // Simple heuristic: prefer products from user's topCategory if exists, then popularity by id mod heuristic
            String topCategory = features != null && features.containsKey("topCategory") ? (String) features.get("topCategory") : null;
            return scoreAndSelect(topCategory, 100);
        } catch (Exception e) {
            log.warn("Feature-store call failed for user {}, falling back to default candidates: {}", userId, e.getMessage());
            return productUniverse.subList(0, 100);
        }
    }

    public Map<String, List<Integer>> getCandidatesBulk(List<String> userIds) {
        Map<String, List<Integer>> map = new HashMap<>();
        for (String uid : userIds) map.put(uid, getCandidates(uid));
        return map;
    }

    private List<Integer> scoreAndSelect(String topCategory, int k) {
        // deterministic scoring: items whose id%5 matches category-hash get bump
        int catHash = topCategory != null ? Math.abs(topCategory.hashCode()) % 5 : -1;
        return productUniverse.stream()
            .sorted(Comparator.comparingInt((Integer id) -> {
                int score = id % 10;
                if (catHash >= 0 && id % 5 == catHash) score += 50;
                return -score;
            }))
            .limit(k)
            .collect(Collectors.toList());
    }
}
