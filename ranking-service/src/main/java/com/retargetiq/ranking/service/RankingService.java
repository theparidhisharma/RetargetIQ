package com.retargetiq.ranking.service;

import com.retargetiq.ranking.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class RankingService {

    public RankResponse rankProducts(RankRequest request) {
        log.info("Ranking {} candidates for user {}", 
                 request.getCandidateProductIds().size(), request.getUserId());

        List<RankedItem> ranked = new ArrayList<>();

        Random random = new Random();

        for (String productId : request.getCandidateProductIds()) {
            // simple scoring for now
            double score = random.nextDouble();

            ranked.add(new RankedItem(productId, score));
        }

        // sort descending
        ranked.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        RankResponse response = new RankResponse();
        response.setUserId(request.getUserId());
        response.setRankedItems(ranked);

        return response;
    }
}
