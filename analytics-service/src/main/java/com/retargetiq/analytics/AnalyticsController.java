package com.retargetiq.analytics;

import com.retargetiq.analytics.model.AnalyticsEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AnalyticsEvent>> getUserEvents(@PathVariable String userId) {
        List<AnalyticsEvent> events = analyticsService.getUserEvents(userId);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<Map<String, Object>> getCampaignAnalytics(
            @PathVariable String campaignId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        Map<String, Object> analytics = analyticsService.getCampaignAnalytics(campaignId, start, end);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getEventSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        Map<String, Long> summary = analyticsService.getEventSummary(start, end);
        return ResponseEntity.ok(summary);
    }
    
    @PostMapping("/event")
    public ResponseEntity<AnalyticsEvent> trackEvent(@RequestBody UserActivity userActivity) {
        AnalyticsEvent event = analyticsService.processUserActivity(userActivity);
        return ResponseEntity.ok(event);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Analytics Service is healthy");
    }
}