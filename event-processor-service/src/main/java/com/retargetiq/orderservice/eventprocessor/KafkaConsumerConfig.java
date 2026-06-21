package com.retargetiq.orderservice.eventprocessor;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import io.micrometer.core.instrument.MeterRegistry;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:event-processor-group}")
    private String groupId;

    private final MeterRegistry meterRegistry;

    public KafkaConsumerConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // ---------------- CONSUMER FACTORY ----------------
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // Deserializers
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.retargetiq.*");

        // Offset behaviour
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // ⭐ Throughput tuning (important for metrics demo)
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
        config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);
        config.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    // ---------------- LISTENER CONTAINER ----------------
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        // ⭐ Parallel consumers → looks great in dashboards
        factory.setConcurrency(3);

        // ⭐ Manual ack = reliability metric + failure recovery
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // ⭐ Attach Micrometer Kafka metrics
        factory.getContainerProperties().setObservationEnabled(true);

        return factory;
    }
}
