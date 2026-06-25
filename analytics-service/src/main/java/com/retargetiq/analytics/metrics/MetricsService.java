package com.retargetiq.analytics.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {

    private final MeterRegistry registry;

    // --------------------------
    // Business Metrics
    // --------------------------

    private final AtomicLong totalViews = new AtomicLong();
    private final AtomicLong totalClicks = new AtomicLong();
    private final AtomicLong totalPurchases = new AtomicLong();
    private final AtomicLong totalAddToCart = new AtomicLong();

    // --------------------------
    // Timers
    // --------------------------

    private final Timer processingTimer;
    private final Timer kafkaLatency;

    public MetricsService(MeterRegistry registry) {

        this.registry = registry;

        Gauge.builder("analytics.total_views", totalViews, AtomicLong::get)
                .description("Total page views")
                .register(registry);

        Gauge.builder("analytics.total_clicks", totalClicks, AtomicLong::get)
                .description("Total clicks")
                .register(registry);

        Gauge.builder("analytics.total_purchases", totalPurchases, AtomicLong::get)
                .description("Total purchases")
                .register(registry);

        Gauge.builder("analytics.total_add_to_cart", totalAddToCart, AtomicLong::get)
                .description("Total add to cart")
                .register(registry);

        processingTimer = Timer.builder("analytics.processing.duration")
                .description("Analytics processing latency")
                .publishPercentiles(0.5,0.90,0.95,0.99)
                .register(registry);

        kafkaLatency = Timer.builder("analytics.kafka.consumer.latency")
                .description("Kafka Consumer Latency")
                .publishPercentiles(0.5,0.90,0.95,0.99)
                .register(registry);
    }

    /**
     * Called every time an event is processed.
     */
    public void recordEventProcessed(String eventType,long processingMs){

        processingTimer.record(processingMs, TimeUnit.MILLISECONDS);

        registry.counter(
                "analytics.events.processed",
                "eventType",
                eventType
        ).increment();

        switch(eventType){

            case "page_view":
                totalViews.incrementAndGet();
                break;

            case "click":
                totalClicks.incrementAndGet();
                break;

            case "purchase":
                totalPurchases.incrementAndGet();
                break;

            case "add_to_cart":
                totalAddToCart.incrementAndGet();
                break;
        }
    }

    public void recordKafkaConsumerLatency(long latency){

        kafkaLatency.record(latency, TimeUnit.MILLISECONDS);

    }

    public void recordProcessingError(String error){

        Counter.builder("analytics.processing.errors")
                .tag("type",error)
                .register(registry)
                .increment();

    }

    public double getConversionRate(){

        if(totalViews.get()==0)
            return 0;

        return ((double)totalPurchases.get())/totalViews.get();

    }

    public long getViews(){

        return totalViews.get();

    }

    public long getClicks(){

        return totalClicks.get();

    }

    public long getPurchases(){

        return totalPurchases.get();

    }

    public long getAddToCart(){

        return totalAddToCart.get();

    }

}