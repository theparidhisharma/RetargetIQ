package com.retargetiq.analytics.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_events")
public class AnalyticsEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String eventType; // page_view, click, purchase, etc.
    
    @Column(columnDefinition = "TEXT")
    private String eventData; // JSON payload
    
    private String pageUrl;
    private String campaignId;
    private String sessionId;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    // Constructors
    public AnalyticsEvent() {}
    
    public AnalyticsEvent(String userId, String eventType, String eventData, 
                         String pageUrl, String campaignId, String sessionId) {
        this.userId = userId;
        this.eventType = eventType;
        this.eventData = eventData;
        this.pageUrl = pageUrl;
        this.campaignId = campaignId;
        this.sessionId = sessionId;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public String getEventData() { return eventData; }
    public void setEventData(String eventData) { this.eventData = eventData; }
    
    public String getPageUrl() { return pageUrl; }
    public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }
    
    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}