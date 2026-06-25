package com.retargetiq.ranking.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks ranking service quality metrics
 * Monitors: computation latency, item scores, diversity, coverage
 * Enables: detection of ranking quality regressions
 */
@Component
public class RankingMetrics {
    
    private final MeterRegistry meterRegistry;
    
    // Latency tracking
    private final Timer rankingComputationTime;
    private final Timer retrievalLatency;
    
    // Quality metrics
    private final DistributionSummary scoreDistribution;
    
    // Counter for SLA violations
    private final AtomicLong slaViolations = new AtomicLong(0);
    
    // Thresholds
    private static final long RANKING_SLA_MS = 100; // Must complete in 100ms
    private static final long RETRIEVAL_SLA_MS = 50;
    
    public RankingMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // ============================================
        // Timer: Computation Time with Percentiles
        // ============================================
        rankingComputationTime = Timer.builder("ranking.computation_time")
            .description("Time to rank items")
            .publishPercentiles(0.5, 0.75, 0.9, 0.95, 0.99)
            .publishPercentileHistogram(true)
            .tag("service", "ranking")
            .register(meterRegistry);
        
        retrievalLatency = Timer.builder("ranking.retrieval_latency")
            .description("Time to fetch candidates from retrieval")
            .publishPercentiles(0.5, 0.75, 0.9, 0.95, 0.99)
            .tag("service", "ranking")
            .register(meterRegistry);
        
        // ============================================
        // Distribution: Score Distribution
        // ============================================
        scoreDistribution = DistributionSummary.builder("ranking.score_distribution")
            .description("Distribution of ranking scores assigned to items")
            .publishPercentiles(0.5, 0.9, 0.99)
            .register(meterRegistry);
        
        // ============================================
        // SLA Monitoring
        // ============================================
        Gauge.builder("ranking.sla_violations_total", slaViolations::get)
            .description("Total number of ranking SLA violations (>100ms)")
            .register(meterRegistry);
        
        // ============================================
        // Ranking Quality Metrics
        // ============================================
        Gauge.builder("ranking.mean_score", () -> scoreDistribution.mean())
            .description("Mean ranking score")
            .register(meterRegistry);
        
        Gauge.builder("ranking.score_std_dev", () -> 
            Math.sqrt(calculateVariance()))
            .description("Standard deviation of ranking scores")
            .register(meterRegistry);
    }
    
    /**
     * Record time taken to rank items
     */
    public void recordRankingComputationTime(long durationMs, int itemsRanked) {
        rankingComputationTime.record(durationMs, TimeUnit.MILLISECONDS);
        
        // Check SLA
        if (durationMs > RANKING_SLA_MS) {
            slaViolations.incrementAndGet();
            meterRegistry.counter("ranking.sla_violations_count",
                "reason", "slow_computation")
                .increment();
        }
        
        meterRegistry.gauge("ranking.items_ranked_per_request",
            itemsRanked);
    }
    
    /**
     * Record retrieval latency
     */
    public void recordRetrievalLatency(long durationMs) {
        retrievalLatency.record(durationMs, TimeUnit.MILLISECONDS);
        
        if (durationMs > RETRIEVAL_SLA_MS) {
            meterRegistry.counter("ranking.sla_violations_count",
                "reason", "slow_retrieval")
                .increment();
        }
    }
    
    /**
     * Record individual item score
     */
    public void recordItemScore(double score) {
        scoreDistribution.record(score);
    }
    
    /**
     * Record ranking quality metrics after users interact
     */
    public void recordRankingQuality(String metric, double value) {
        meterRegistry.gauge("ranking.quality_" + metric, value);
    }
    
    /**
     * Record position-based click-through rate
     * Tracks: Did users click on items at each ranking position?
     */
    public void recordPositionClick(int position) {
        meterRegistry.counter("ranking.position_clicks",
            "position", String.valueOf(position))
            .increment();
    }
    
    /**
     * Record position-based impression (item was shown)
     */
    public void recordPositionImpression(int position) {
        meterRegistry.counter("ranking.position_impressions",
            "position", String.valueOf(position))
            .increment();
    }
    
    /**
     * Record Mean Reciprocal Rank (MRR)
     * Higher is better: if first item clicked, MRR=1; if 5th item, MRR=0.2
     */
    public void recordMRR(double mrr) {
        meterRegistry.timer("ranking.mrr")
            .record((long)(mrr * 1000), TimeUnit.MILLISECONDS);
    }
    
    /**
     * Record catalog coverage (% of items that appear in rankings)
     */
    public void recordCatalogCoverage(double coverage) {
        meterRegistry.gauge("ranking.catalog_coverage", coverage);
    }
    
    /**
     * Record diversity of ranked items (how varied are categories?)
     */
    public void recordDiversity(double diversity) {
        meterRegistry.gauge("ranking.diversity_index", diversity);
    }
    
    /**
     * Record error in ranking
     */
    public void recordRankingError(String errorType) {
        meterRegistry.counter("ranking.errors_total",
            "error_type", errorType)
            .increment();
    }
    
    // ========================================
    // UTILITY METHODS
    // ========================================
    
    private double calculateVariance() {
        double mean = scoreDistribution.mean();
        // Simplified: would need to track individual values for true variance
        return 0.0; // Placeholder
    }
    
    /**
     * Get all ranking metrics for monitoring
     */
    public Map<String, Object> getMetricsSummary() {
        return Map.of(
            "p50_latency_ms", rankingComputationTime.takeSnapshot().percentileValues()[0].value(TimeUnit.MILLISECONDS),
            "p99_latency_ms", rankingComputationTime.takeSnapshot().percentileValues()[4].value(TimeUnit.MILLISECONDS),
            "mean_score", scoreDistribution.mean(),
            "sla_violations", slaViolations.get(),
            "call_count", rankingComputationTime.count()
        );
    }
}