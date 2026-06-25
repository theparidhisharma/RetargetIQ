package com.retargetiq.analytics;

import com.retargetiq.analytics.model.AnalyticsEvent;
import com.retargetiq.analytics.metrics.MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final MetricsService metricsService;

    public AnalyticsController(
            AnalyticsService analyticsService,
            MetricsService metricsService) {

        this.analyticsService = analyticsService;
        this.metricsService = metricsService;
    }

    /**
     * Save an event directly (useful for testing)
     */
    @PostMapping("/event")
    public ResponseEntity<AnalyticsEvent> trackEvent(
            @RequestBody UserActivity activity) {

        AnalyticsEvent event =
                analyticsService.processUserActivity(activity);

        return ResponseEntity.ok(event);
    }

    /**
     * All events for one user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AnalyticsEvent>> getUserEvents(
            @PathVariable String userId) {

        return ResponseEntity.ok(
                analyticsService.getUserEvents(userId)
        );
    }

    /**
     * Campaign events
     */
    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<List<AnalyticsEvent>> getCampaignEvents(
            @PathVariable String campaignId) {

        return ResponseEntity.ok(
                analyticsService.getCampaignEvents(campaignId)
        );
    }

    /**
     * Dashboard summary
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String,Object>> summary(){

        Map<String,Object> response = new HashMap<>();

        response.put("views",
                metricsService.getViews());

        response.put("clicks",
                metricsService.getClicks());

        response.put("purchases",
                metricsService.getPurchases());

        response.put("addToCart",
                metricsService.getAddToCart());

        response.put("conversionRate",
                metricsService.getConversionRate());

        return ResponseEntity.ok(response);
    }

    /**
     * Health endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health(){

        return ResponseEntity.ok(
                "Analytics Service Running"
        );

    }

}