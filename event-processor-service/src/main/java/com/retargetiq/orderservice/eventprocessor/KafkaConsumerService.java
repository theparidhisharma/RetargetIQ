package com.retargetiq.orderservice.eventprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(
        topics = "user-activity", 
        groupId = "event-processor-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserActivity(
            @Payload Object message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        
        try {
            logger.info("🎯 Consumed message from Kafka - Topic: {}, Partition: {}, Key: {}", 
                       topic, partition, key);
            logger.info("Message: {}", message);
            
            // Process the message
            processUserActivity(message);
            
        } catch (Exception e) {
            logger.error("❌ Error processing message from topic {}: {}", topic, e.getMessage(), e);
        }
    }

    @KafkaListener(
        topics = "order-events", 
        groupId = "event-processor-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderEvents(@Payload Object message) {
        try {
            logger.info("🛒 Processing order event: {}", message);
            processOrderEvent(message);
        } catch (Exception e) {
            logger.error("❌ Error processing order event: {}", e.getMessage(), e);
        }
    }

    private void processUserActivity(Object userActivity) {
        // Implement your user activity processing logic
        logger.info("Processing user activity: {}", userActivity);
        
        // Example: Extract and process user behavior data
        // Save to database, trigger recommendations, etc.
    }

    private void processOrderEvent(Object orderEvent) {
        // Implement your order event processing logic
        logger.info("Processing order event: {}", orderEvent);
        
        // Example: Update order status, calculate metrics, etc.
    }
}