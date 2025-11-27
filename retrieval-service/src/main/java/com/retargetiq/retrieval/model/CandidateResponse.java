package com.retargetiq.retrieval.model;

import lombok.Data;

import java.util.List;

@Data
public class CandidateResponse {

    @Data
    public static class Candidate {
        private String itemId;
        private double score;
    }

    private String userId;
    private List<Candidate> candidates;
}
