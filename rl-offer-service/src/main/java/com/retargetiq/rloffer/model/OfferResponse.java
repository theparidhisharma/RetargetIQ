package com.retargetiq.rloffer.model;

import lombok.Data;

@Data
public class OfferResponse {
    private String userId;
    private String offerType;   // e.g. "DISCOUNT_10", "REMINDER", "NO_ACTION"
    private String itemId;      // top-ranked item
    private double rewardScore; // confidence
}
