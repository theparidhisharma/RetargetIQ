package com.retargetiq.ranking.model;

import lombok.Data;
import java.util.List;

@Data
public class RankResponse {
    private String userId;
    private List<RankedItem> rankedItems;
}
