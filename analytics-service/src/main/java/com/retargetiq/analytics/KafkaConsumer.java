package com.retargetiq.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
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
    private MeterRegistry meterRegistry;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Metrics
    private Counter eventsConsumedCounter;
    private Counter eventsFailedCounter;

    @PostConstruct
    public void initMetrics() {
        eventsConsumedCounter = Counter.builder("retargetiq_events_consumed_total")
                .description("Total Kafka events consumed by analytics-service")
                .register(meterRegistry);

        eventsFailedCounter = Counter.builder("retargetiq_event_processing_failures_total")
                .description("Total failed Kafka event processing attempts")
                .register(meterRegistry);
    }

    // ---------- USER ACTIVITY TOPIC ----------
    @KafkaListener(topics = "user-activities", groupId = "analytics-group")
    public void consumeUserActivity(String message) {

        // ⭐ increment as soon as message arrives
        eventsConsumedCounter.increment();

        try {
            logger.info("Received user activity: {}", message);

            UserActivity userActivity = objectMapper.readValue(message, UserActivity.class);
            analyticsService.processUserActivity(userActivity);

            logger.info("Successfully processed user activity for user: {}", userActivity.getUserId());

        } catch (Exception e) {
            eventsFailedCounter.increment(); // ⭐ track failures
            logger.error("Error processing user activity message: {}", message, e);
        }
    }

    // ---------- CAMPAIGN EVENTS TOPIC ----------
    @KafkaListener(topics = "campaign-events", groupId = "analytics-group")
    public void consumeCampaignEvent(String message) {

        // ⭐ increment here too (for every topic)
        eventsConsumedCounter.increment();

        try {
            logger.info("Received campaign event: {}", message);

            UserActivity campaignEvent = objectMapper.readValue(message, UserActivity.class);
            analyticsService.processUserActivity(campaignEvent);

            logger.info("Successfully processed campaign event for campaign: {}", campaignEvent.getCampaignId());

        } catch (Exception e) {
            eventsFailedCounter.increment();
            logger.error("Error processing campaign event message: {}", message, e);
        }
    }
}
