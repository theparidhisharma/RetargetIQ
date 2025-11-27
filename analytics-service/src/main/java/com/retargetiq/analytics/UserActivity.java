package com.retargetiq.analytics;

import java.time.LocalDateTime;

public class UserActivity {
    private String userId;
    private String eventType;
    private String pageUrl;
    private String campaignId;
    private String sessionId;
    private Object eventData;
    private LocalDateTime timestamp;
    
    // Constructors
    public UserActivity() {}
    
    public UserActivity(String userId, String eventType, String pageUrl, 
                       String campaignId, String sessionId, Object eventData) {
        this.userId = userId;
        this.eventType = eventType;
        this.pageUrl = pageUrl;
        this.campaignId = campaignId;
        this.sessionId = sessionId;
        this.eventData = eventData;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public String getPageUrl() { return pageUrl; }
    public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }
    
    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public Object getEventData() { return eventData; }
    public void setEventData(Object eventData) { this.eventData = eventData; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}