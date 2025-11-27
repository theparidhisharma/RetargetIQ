package com.retargetiq.ranking.model;

import lombok.Data;
import java.util.List;

@Data
public class RankRequest {
    private String userId;
    private List<String> candidateProductIds;
}
