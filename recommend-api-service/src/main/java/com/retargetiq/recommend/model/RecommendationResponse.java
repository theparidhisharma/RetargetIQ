package com.retargetiq.recommend.model;

import java.util.List;
import java.util.Map;

public class RecommendationResponse {
    private String userId;
    private List<RankedItem> recommendations;
    private Map<String, Object> offer; // flexible: discount, bundle, id, etc.

    public RecommendationResponse() {}

    public RecommendationResponse(String userId, List<RankedItem> recommendations, Map<String, Object> offer) {
        this.userId = userId;
        this.recommendations = recommendations;
        this.offer = offer;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<RankedItem> getRecommendations() { return recommendations; }
    public void setRecommendations(List<RankedItem> recommendations) { this.recommendations = recommendations; }

    public Map<String, Object> getOffer() { return offer; }
    public void setOffer(Map<String, Object> offer) { this.offer = offer; }
}
