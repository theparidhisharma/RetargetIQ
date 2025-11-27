package com.retargetiq.retrieval.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retargetiq.retrieval.service.RetrievalService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/retrieve")
@RequiredArgsConstructor
public class RetrievalController {
    private final RetrievalService retrievalService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Integer>> retrieve(@PathVariable String userId) {
        List<Integer> candidates = retrievalService.getCandidates(userId);
        return ResponseEntity.ok(candidates);
    }

    @PostMapping("/bulk")
    public ResponseEntity<Map<String, List<Integer>>> retrieveBulk(@RequestBody List<String> userIds) {
        return ResponseEntity.ok(retrievalService.getCandidatesBulk(userIds));
    }
}
