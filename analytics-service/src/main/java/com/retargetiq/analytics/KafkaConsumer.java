package com.retargetiq.analytics;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retargetiq.analytics.metrics.MetricsService;
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

    @Autowired
    private MetricsService metricsService;

    private final ObjectMapper objectMapper = new ObjectMapper();
   
    
    // ---------- USER ACTIVITY TOPIC ----------
    @KafkaListener(topics = "user-activity", groupId = "analytics-group")
    public void consumeUserActivity(String message) {
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("Received user activity: {}", message);

            // Parse and process
            UserActivity userActivity = objectMapper.readValue(message, UserActivity.class);
            analyticsService.processUserActivity(userActivity);

            // Calculate and record metrics
            long processingTime = System.currentTimeMillis() - startTime;
            metricsService.recordEventProcessed(userActivity.getEventType(), processingTime);
            metricsService.recordKafkaConsumerLatency(processingTime);

            logger.info("Successfully processed user activity for user: {} ({}ms)", 
                userActivity.getUserId(), processingTime);

        } catch (Exception e) {
            metricsService.recordProcessingError("user_activity_processing");
            logger.error("Error processing user activity message: {}", message, e);
        }
    }

    // ---------- CAMPAIGN EVENTS TOPIC ----------
    @KafkaListener(topics = "campaign-events", groupId = "analytics-group")
    public void consumeCampaignEvent(String message) {
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("Received campaign event: {}", message);

            // Parse and process
            UserActivity campaignEvent = objectMapper.readValue(message, UserActivity.class);
            analyticsService.processUserActivity(campaignEvent);

            // Calculate and record metrics
            long processingTime = System.currentTimeMillis() - startTime;
            metricsService.recordEventProcessed(
                campaignEvent.getEventType(), 
                processingTime
            );
            metricsService.recordKafkaConsumerLatency(processingTime);

            logger.info("Successfully processed campaign event ({}ms)", processingTime);

        } catch (Exception e) {
            metricsService.recordProcessingError("campaign_event_processing");
            logger.error("Error processing campaign event message: {}", message, e);
        }
    }
}