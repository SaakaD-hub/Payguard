package com.payguard.reconciliation.repository;

import com.payguard.reconciliation.model.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
    Optional<Settlement> findBySettlementDate(LocalDate date);
}