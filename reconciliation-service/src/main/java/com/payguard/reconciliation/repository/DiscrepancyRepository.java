package com.payguard.reconciliation.repository;

import com.payguard.reconciliation.model.Discrepancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiscrepancyRepository extends JpaRepository<Discrepancy, UUID> {
    List<Discrepancy> findBySettlementId(UUID settlementId);
    List<Discrepancy> findByResolvedFalse();
}