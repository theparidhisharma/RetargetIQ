package com.retargetiq.analytics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retargetiq.analytics.model.AnalyticsEvent;
import com.retargetiq.analytics.repository.AnalyticsRepository;
import com.retargetiq.analytics.metrics.MetricsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final MetricsService metricsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalyticsService(
            AnalyticsRepository analyticsRepository,
            MetricsService metricsService) {

        this.analyticsRepository = analyticsRepository;
        this.metricsService = metricsService;
    }

    public AnalyticsEvent processUserActivity(UserActivity userActivity) {

        try {

            String eventData =
                    objectMapper.writeValueAsString(userActivity.getEventData());

            AnalyticsEvent event =
                    new AnalyticsEvent(
                            userActivity.getUserId(),
                            userActivity.getEventType(),
                            eventData,
                            userActivity.getPageUrl(),
                            userActivity.getCampaignId(),
                            userActivity.getSessionId()
                    );

            AnalyticsEvent saved = analyticsRepository.save(event);

            metricsService.recordEventProcessed(
                    userActivity.getEventType(),
                    0
            );

            return saved;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    
    public List<AnalyticsEvent> getUserEvents(String userId) {
        return analyticsRepository.findByUserId(userId);
    }

    public List<AnalyticsEvent> getCampaignEvents(String campaignId) {
        return analyticsRepository.findByCampaignId(campaignId);
    }

    public List<AnalyticsEvent> getEventsBetween(
            LocalDateTime start,
            LocalDateTime end) {

        return analyticsRepository.findByTimestampBetween(start, end);
    }
}