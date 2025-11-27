package com.retargetiq.featurestore.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "user_features")
@Data
@NoArgsConstructor
public class UserFeature {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "visit_count")
    private Long visitCount = 0L;

    @Column(name = "last_activity_ts")
    private Instant lastActivityTs;

    @Column(name = "total_spend")
    private Double totalSpend = 0.0;

    @Column(name = "preferred_category")
    private String preferredCategory;

    // A JSON string for flexible fields (category affinities, embeddings if needed)
    @Column(name = "meta", columnDefinition = "text")
    private String meta;

    // convenience constructor
    public UserFeature(String userId) {
        this.userId = userId;
        this.lastActivityTs = Instant.now();
    }
}
