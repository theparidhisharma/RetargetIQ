package com.retargetiq.featurestore.controller;

import com.retargetiq.featurestore.model.UserFeature;
import com.retargetiq.featurestore.repo.UserFeatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/features")
@RequiredArgsConstructor
public class FeatureController {

    private final UserFeatureRepository repo;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getFeatures(@PathVariable String userId) {
        Optional<UserFeature> opt = repo.findById(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping("/{userId}/reset")
    public ResponseEntity<?> resetFeatures(@PathVariable String userId) {
        repo.deleteById(userId);
        return ResponseEntity.ok().build();
    }
}
