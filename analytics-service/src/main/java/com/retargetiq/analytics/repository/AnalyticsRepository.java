package com.retargetiq.analytics.repository;

import com.retargetiq.analytics.model.AnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<AnalyticsEvent, Long> {
    
    List<AnalyticsEvent> findByUserId(String userId);
    
    List<AnalyticsEvent> findByEventType(String eventType);
    
    List<AnalyticsEvent> findByCampaignId(String campaignId);
    
    List<AnalyticsEvent> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(e) FROM AnalyticsEvent e WHERE e.eventType = :eventType AND e.timestamp BETWEEN :start AND :end")
    Long countByEventTypeAndTimestampBetween(@Param("eventType") String eventType, 
                                           @Param("start") LocalDateTime start, 
                                           @Param("end") LocalDateTime end);
    
    @Query("SELECT e.campaignId, COUNT(e) FROM AnalyticsEvent e WHERE e.timestamp BETWEEN :start AND :end GROUP BY e.campaignId")
    List<Object[]> countEventsByCampaign(@Param("start") LocalDateTime start, 
                                        @Param("end") LocalDateTime end);
}