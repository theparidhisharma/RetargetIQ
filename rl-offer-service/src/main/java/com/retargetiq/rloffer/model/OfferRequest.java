package com.retargetiq.rloffer.model;

import lombok.Data;
import java.util.List;

@Data
public class OfferRequest {
    private String userId;
    private UserFeatures userFeatures;
    private List<CandidateItem> rankedItems;
}
