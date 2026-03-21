package com.payguard.payment.repository;

import com.payguard.payment.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByMerchantIdOrderByCreatedAtDesc(UUID merchantId);
    Optional<Transaction> findByStripePaymentId(String stripePaymentId);
}