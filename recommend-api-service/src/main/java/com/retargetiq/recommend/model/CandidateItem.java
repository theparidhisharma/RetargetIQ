package com.retargetiq.recommend.model;

import java.util.Map;

public class CandidateItem {
    private String itemId;
    private double score;
    private Map<String, Object> metadata;

    public CandidateItem() {}

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
