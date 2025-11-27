package com.retargetiq.orderservice.eventprocessor;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ServiceHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // Add custom health checks here
            return Health.up()
                    .withDetail("service", "event-processor-service")
                    .withDetail("status", "operational")
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("service", "event-processor-service")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}