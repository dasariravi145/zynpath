package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.AdMobSsvRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdMobSsvRecordRepository extends JpaRepository<AdMobSsvRecordEntity, String> {
    Optional<AdMobSsvRecordEntity> findByClaimId(String claimId);
}
