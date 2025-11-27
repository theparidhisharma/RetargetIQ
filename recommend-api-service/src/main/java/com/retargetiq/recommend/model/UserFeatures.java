package com.retargetiq.recommend.model;

import java.util.Map;

public class UserFeatures {
    private String userId;
    private long lastActiveTs;
    private int visitCount;
    private double churnProb;
    private Map<String, Double> categoryAffinity;

    public UserFeatures() {}

    // getters & setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getLastActiveTs() { return lastActiveTs; }
    public void setLastActiveTs(long lastActiveTs) { this.lastActiveTs = lastActiveTs; }

    public int getVisitCount() { return visitCount; }
    public void setVisitCount(int visitCount) { this.visitCount = visitCount; }

    public double getChurnProb() { return churnProb; }
    public void setChurnProb(double churnProb) { this.churnProb = churnProb; }

    public Map<String, Double> getCategoryAffinity() { return categoryAffinity; }
    public void setCategoryAffinity(Map<String, Double> categoryAffinity) { this.categoryAffinity = categoryAffinity; }
}
