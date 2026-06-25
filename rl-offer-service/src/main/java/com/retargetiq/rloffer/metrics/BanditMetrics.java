package com.retargetiq.rloffer.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks Multi-Armed Bandit performance
 * Monitors: arm selection rate, conversion rate, mean reward per arm
 * Enables: optimal offer strategy selection and A/B testing
 */
@Component
public class BanditMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Map<String, ArmStats> armStats;
    
    static class ArmStats {
        final AtomicLong selections = new AtomicLong(0);
        final AtomicLong conversions = new AtomicLong(0);
        final AtomicLong totalRewardCents = new AtomicLong(0);
        final AtomicLong explorations = new AtomicLong(0);
    }
    
    public BanditMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.armStats = new ConcurrentHashMap<>();
        
        // Initialize all offer arms
        initializeArms(
            "DISCOUNT_10", 
            "DISCOUNT_20", 
            "DISCOUNT_30",
            "REMINDER", 
            "NO_ACTION"
        );
    }
    
    /**
     * Initialize tracking for each offer type
     */
    private void initializeArms(String... arms) {
        for (String arm : arms) {
            armStats.put(arm, new ArmStats());
            
            // Register per-arm conversion rate gauge
            Gauge.builder("bandit.arm.conversion_rate", 
                () -> getConversionRate(arm))
                .description("Conversion rate for offer: " + arm)
                .tag("arm", arm)
                .register(meterRegistry);
            
            // Register per-arm mean reward gauge
            Gauge.builder("bandit.arm.mean_reward", 
                () -> getMeanReward(arm))
                .description("Mean reward (USD) for offer: " + arm)
                .tag("arm", arm)
                .register(meterRegistry);
            
            // Register per-arm selection count
            Gauge.builder("bandit.arm.selections_total",
                () -> getSelectionCount(arm))
                .description("Total times offer selected: " + arm)
                .tag("arm", arm)
                .register(meterRegistry);
        }
    }
    
    /**
     * Record that an arm (offer) was selected for a user
     */
    public void recordArmSelection(String arm, boolean isExploration) {
        ArmStats stats = armStats.get(arm);
        if (stats == null) return;
        
        stats.selections.incrementAndGet();
        if (isExploration) {
            stats.explorations.incrementAndGet();
        }
        
        // Counter: incremented each time
        meterRegistry.counter("bandit.arm.selected_total", 
            "arm", arm,
            "exploration", String.valueOf(isExploration))
            .increment();
        
        // Gauge: exploration rate per arm
        double explorationRate = (double) stats.explorations.get() / stats.selections.get();
        meterRegistry.gauge("bandit.arm.exploration_rate",
            explorationRate,
            Tag.of("arm", arm));
    }
    
    /**
     * Record conversion/reward for selected arm
     * Call this when user converts after seeing the offer
     */
    public void recordConversion(String arm, double rewardUSD) {
        ArmStats stats = armStats.get(arm);
        if (stats == null) return;
        
        stats.conversions.incrementAndGet();
        stats.totalRewardCents.addAndGet((long)(rewardUSD * 100));
        
        meterRegistry.counter("bandit.arm.conversions_total", "arm", arm).increment();
        meterRegistry.gauge("bandit.arm.last_reward_usd", 
            rewardUSD, 
            Tag.of("arm", arm));
    }
    
    /**
     * Record non-conversion (no reward)
     */
    public void recordNonConversion(String arm) {
        ArmStats stats = armStats.get(arm);
        if (stats == null) return;
        
        meterRegistry.counter("bandit.arm.non_conversions_total", "arm", arm).increment();
    }
    
    // ========================================
    // GETTERS: For dashboards and APIs
    // ========================================
    
    /**
     * Get conversion rate for a specific arm
     */
    public double getConversionRate(String arm) {
        ArmStats stats = armStats.get(arm);
        if (stats == null) return 0.0;
        
        long selections = stats.selections.get();
        if (selections == 0) return 0.0;
        
        return (double) stats.conversions.get() / selections;
    }
    
    /**
     * Get average reward (USD) for a specific arm
     */
    public double getMeanReward(String arm) {
        ArmStats stats = armStats.get(arm);
        if (stats == null) return 0.0;
        
        long conversions = stats.conversions.get();
        if (conversions == 0) return 0.0;
        
        return (double) stats.totalRewardCents.get() / conversions / 100.0;
    }
    
    /**
     * Get total selection count for arm
     */
    public long getSelectionCount(String arm) {
        ArmStats stats = armStats.get(arm);
        return stats != null ? stats.selections.get() : 0;
    }
    
    /**
     * Get exploration rate for arm (0.0 to 1.0)
     */
    public double getExplorationRate(String arm) {
        ArmStats stats = armStats.get(arm);
        if (stats == null) return 0.0;
        
        long selections = stats.selections.get();
        if (selections == 0) return 0.0;
        
        return (double) stats.explorations.get() / selections;
    }
    
    /**
     * Return performance of all arms
     */
    public Map<String, Object> getAllArmPerformance() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        for (String arm : armStats.keySet()) {
            ArmStats stats = armStats.get(arm);
            
            result.put(arm, Map.of(
                "selections", stats.selections.get(),
                "conversions", stats.conversions.get(),
                "conversion_rate", getConversionRate(arm),
                "mean_reward_usd", getMeanReward(arm),
                "total_reward_usd", (double) stats.totalRewardCents.get() / 100.0,
                "exploration_rate", getExplorationRate(arm)
            ));
        }
        
        return result;
    }
    
    /**
     * Get best performing arm (highest mean reward)
     */
    public String getBestArm() {
        return armStats.keySet().stream()
            .max((a, b) -> Double.compare(getMeanReward(a), getMeanReward(b)))
            .orElse("NONE");
    }
    
    /**
     * Calculate regret (lost opportunity from suboptimal choices)
     * Regret = (best_arm_reward - chosen_arm_reward) per selection
     */
    public double getRegret(String arm) {
        double bestReward = getMeanReward(getBestArm());
        double armReward = getMeanReward(arm);
        double difference = Math.max(0, bestReward - armReward);
        
        // Total regret = difference * times selected
        return difference * getSelectionCount(arm);
    }
    
    /**
     * Calculate cumulative regret across all arms
     */
    public double getTotalRegret() {
        return armStats.keySet().stream()
            .mapToDouble(this::getRegret)
            .sum();
    }
}