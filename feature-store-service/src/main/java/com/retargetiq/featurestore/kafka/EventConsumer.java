package com.retargetiq.featurestore.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retargetiq.featurestore.model.UserFeature;
import com.retargetiq.featurestore.repo.UserFeatureRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventConsumer {

    private final UserFeatureRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "${featurestore.kafka.user-topic:user-events}", groupId = "feature-store")
    public void handleUserEvents(String message) {
        try {
            JsonNode node = mapper.readTree(message);
            String userId = node.path("userId").asText(null);
            String eventType = node.path("type").asText("unknown");
            long ts = node.path("ts").asLong(System.currentTimeMillis());

            if (userId == null || userId.isBlank()) {
                log.warn("user-event without userId: {}", message);
                return;
            }

            UserFeature f = repo.findById(userId).orElse(new UserFeature(userId));
            // update basic features
            if ("view".equalsIgnoreCase(eventType) || "click".equalsIgnoreCase(eventType)) {
                f.setVisitCount((f.getVisitCount() == null ? 0L : f.getVisitCount()) + 1);
            }

            f.setLastActivityTs(Instant.ofEpochMilli(ts));

            // optionally update preferredCategory from event
            String category = node.path("category").asText(null);
            if (category != null && !category.isBlank()) {
                f.setPreferredCategory(category);
            }

            repo.save(f);
            log.debug("Updated feature for user {}", userId);
        } catch (Exception e) {
            log.error("Failed to process user-event: {}", message, e);
        }
    }

    @KafkaListener(topics = "${featurestore.kafka.order-topic:order-events}", groupId = "feature-store")
    public void handleOrderEvents(String message) {
        try {
            JsonNode node = mapper.readTree(message);
            String userId = node.path("userId").asText(null);
            double amount = node.path("amount").asDouble(0.0);
            long ts = node.path("ts").asLong(System.currentTimeMillis());

            if (userId == null || userId.isBlank()) {
                log.warn("order-event without userId: {}", message);
                return;
            }

            UserFeature f = repo.findById(userId).orElse(new UserFeature(userId));
            f.setTotalSpend((f.getTotalSpend() == null ? 0.0 : f.getTotalSpend()) + amount);
            f.setLastActivityTs(Instant.ofEpochMilli(ts));
            repo.save(f);
            log.debug("Updated order feature for user {}", userId);
        } catch (Exception e) {
            log.error("Failed to process order-event: {}", message, e);
        }
    }
}
