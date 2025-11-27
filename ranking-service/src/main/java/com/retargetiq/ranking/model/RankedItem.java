package com.retargetiq.ranking.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankedItem {
    private String productId;
    private double score;
}
