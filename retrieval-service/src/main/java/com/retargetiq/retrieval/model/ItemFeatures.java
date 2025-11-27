package com.retargetiq.retrieval.model;

import lombok.Data;

import java.util.List;

/**
 * Minimal item features.
 */
@Data
public class ItemFeatures {
    private String itemId;
    private String title;
    private List<Double> embedding;   // item embedding vector
    private Double price;
    private Integer popularity;       // simple popularity metric
    // extend if needed
}
