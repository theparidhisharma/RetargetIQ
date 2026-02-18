package com.retargetiq.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    
    @Autowired
    private AnalyticsService analyticsService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @KafkaListener(topics = "user-activities", groupId = "analytics-group")
    public void consumeUserActivity(String message) {
        try {
            logger.info("Received user activity: {}", message);
            
            UserActivity userActivity = objectMapper.readValue(message, UserActivity.class);
            analyticsService.processUserActivity(userActivity);
            
            logger.info("Successfully processed user activity for user: {}", userActivity.getUserId());
            
        } catch (Exception e) {
            logger.error("Error processing user activity message: {}", message, e);
        }
    }
    
    @KafkaListener(topics = "campaign-events", groupId = "analytics-group")
    public void consumeCampaignEvent(String message) {
        try {
            logger.info("Received campaign event: {}", message);
            
            UserActivity campaignEvent = objectMapper.readValue(message, UserActivity.class);
            analyticsService.processUserActivity(campaignEvent);
            
            logger.info("Successfully processed campaign event for campaign: {}", campaignEvent.getCampaignId());
            
        } catch (Exception e) {
            logger.error("Error processing campaign event message: {}", message, e);
        }
    }
}