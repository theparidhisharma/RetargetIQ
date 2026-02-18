package com.retargetiq.analytics;

import com.retargetiq.analytics.model.AnalyticsEvent;
import com.retargetiq.analytics.repository.AnalyticsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {
    
    @Autowired
    private AnalyticsRepository analyticsRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public AnalyticsEvent processUserActivity(UserActivity userActivity) {
        try {
            String eventData = objectMapper.writeValueAsString(userActivity.getEventData());
            
            AnalyticsEvent event = new AnalyticsEvent(
                userActivity.getUserId(),
                userActivity.getEventType(),
                eventData,
                userActivity.getPageUrl(),
                userActivity.getCampaignId(),
                userActivity.getSessionId()
            );
            
            return analyticsRepository.save(event);
            
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process user activity", e);
        }
    }
    
    public List<AnalyticsEvent> getUserEvents(String userId) {
        return analyticsRepository.findByUserId(userId);
    }
    
    public Map<String, Object> getCampaignAnalytics(String campaignId, LocalDateTime start, LocalDateTime end) {
        List<AnalyticsEvent> campaignEvents = analyticsRepository.findByCampaignId(campaignId)
            .stream()
            .filter(event -> !event.getTimestamp().isBefore(start) && !event.getTimestamp().isAfter(end))
            .toList();
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalEvents", campaignEvents.size());
        analytics.put("uniqueUsers", campaignEvents.stream().map(AnalyticsEvent::getUserId).distinct().count());
        analytics.put("pageViews", campaignEvents.stream().filter(e -> "page_view".equals(e.getEventType())).count());
        analytics.put("clicks", campaignEvents.stream().filter(e -> "click".equals(e.getEventType())).count());
        analytics.put("conversions", campaignEvents.stream().filter(e -> "purchase".equals(e.getEventType())).count());
        
        return analytics;
    }
    
    public Map<String, Long> getEventSummary(LocalDateTime start, LocalDateTime end) {
        List<AnalyticsEvent> events = analyticsRepository.findByTimestampBetween(start, end);
        
        Map<String, Long> summary = new HashMap<>();
        summary.put("page_views", events.stream().filter(e -> "page_view".equals(e.getEventType())).count());
        summary.put("clicks", events.stream().filter(e -> "click".equals(e.getEventType())).count());
        summary.put("purchases", events.stream().filter(e -> "purchase".equals(e.getEventType())).count());
        summary.put("add_to_cart", events.stream().filter(e -> "add_to_cart".equals(e.getEventType())).count());
        
        return summary;
    }
}