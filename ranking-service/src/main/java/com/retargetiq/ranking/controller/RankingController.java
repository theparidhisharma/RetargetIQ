package com.retargetiq.ranking.controller;

import com.retargetiq.ranking.model.*;
import com.retargetiq.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rank")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @PostMapping
    public RankResponse rank(@RequestBody RankRequest request) {
        return rankingService.rankProducts(request);
    }
}
