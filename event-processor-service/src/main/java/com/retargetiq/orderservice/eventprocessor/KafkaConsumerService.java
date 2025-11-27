package com.example.orderservice.eventprocessor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "user-activity", groupId = "group_id")
    public void consume(String message) {
        System.out.println("🎯 Consumed message from Kafka: " + message);
    }
}
