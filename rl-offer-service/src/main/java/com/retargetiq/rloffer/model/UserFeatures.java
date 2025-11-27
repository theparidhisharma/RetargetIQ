package com.retargetiq.rloffer.model;

import lombok.Data;

@Data
public class UserFeatures {
    private String userId;
    private double activityScore;
    private double purchaseProbability;
    private double recencyScore;
}
