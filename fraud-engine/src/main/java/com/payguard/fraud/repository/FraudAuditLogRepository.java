package com.payguard.fraud.repository;

import com.payguard.fraud.model.FraudAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FraudAuditLogRepository extends JpaRepository<FraudAuditLog, UUID> {
    List<FraudAuditLog> findByMerchantIdOrderByCreatedAtDesc(UUID merchantId);
    List<FraudAuditLog> findByTransactionId(UUID transactionId);
}