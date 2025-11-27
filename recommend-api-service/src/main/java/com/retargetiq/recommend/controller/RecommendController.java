package com.retargetiq.recommend.controller;

import com.retargetiq.recommend.model.RecommendationResponse;
import com.retargetiq.recommend.service.RecommendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommend")
public class RecommendController {
    private final RecommendService recommendService;
    private final Logger logger = LoggerFactory.getLogger(RecommendController.class);

    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<RecommendationResponse> recommend(@PathVariable String userId,
                                                            @RequestParam(required = false, defaultValue = "10") int k) {
        logger.info("Received recommend request for userId={} k={}", userId, k);
        RecommendationResponse resp = recommendService.getRecommendations(userId, k);
        if (resp == null) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok(resp);
    }
}
