package com.retargetiq.retrieval.model;

import lombok.Data;

import java.util.List;

/**
 * Minimal user features returned by feature-store-service.
 * Keep this small and extend as you need.
 */
@Data
public class UserFeatures {
    private String userId;
    private List<Double> embedding;   // user embedding vector
    private Long lastActiveAt;        // epoch millis
    private Integer totalVisits;
    private Double lifetimeValue;
    // add more fields later
}
