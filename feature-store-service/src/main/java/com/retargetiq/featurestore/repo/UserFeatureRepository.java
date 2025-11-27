package com.retargetiq.featurestore.repo;

import com.retargetiq.featurestore.model.UserFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFeatureRepository extends JpaRepository<UserFeature, String> {
}
