package com.example.orderservice.useractivity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;


@Service
public class KafkaProducer {

    private static final String TOPIC = "user-activity";


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendActivity(String userId, String action) {
        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("userId", userId);
            payload.put("eventType", action);
            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send("user-activity", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
