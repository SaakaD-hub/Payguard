package com.payguard.reconciliation.service;

import com.payguard.reconciliation.dto.StripeBalanceTransaction;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.BalanceTransaction;
import com.stripe.model.BalanceTransactionCollection;
import com.stripe.param.BalanceTransactionListParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Stripe Report Service - Fetches balance transactions from Stripe
 */
@Service
@Slf4j
public class StripeReportService {

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public List<StripeBalanceTransaction> getBalanceTransactionsForDate(LocalDate date) {
        log.info("Fetching Stripe balance transactions for date: {}", date);
        
        try {
            long startTimestamp = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
            long endTimestamp = date.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC);
            
            BalanceTransactionListParams params = BalanceTransactionListParams.builder()
                    .setCreated(BalanceTransactionListParams.Created.builder()
                            .setGte(startTimestamp)
                            .setLt(endTimestamp)
                            .build())
                    .setLimit(100L)
                    .build();
            
            BalanceTransactionCollection transactions = BalanceTransaction.list(params);
            
            List<StripeBalanceTransaction> result = new ArrayList<>();
            for (BalanceTransaction txn : transactions.getData()) {
                result.add(StripeBalanceTransaction.builder()
                        .id(txn.getId())
                        .amount(BigDecimal.valueOf(txn.getAmount()).divide(BigDecimal.valueOf(100)))
                        .currency(txn.getCurrency())
                        .created(Instant.ofEpochSecond(txn.getCreated()).atZone(ZoneOffset.UTC).toLocalDateTime())
                        .type(txn.getType())
                        .build());
            }
            
            log.info("Retrieved {} Stripe transactions for {}", result.size(), date);
            return result;
            
        } catch (StripeException e) {
            log.error("Failed to fetch Stripe balance transactions", e);
            return new ArrayList<>();
        }
    }

    public BigDecimal getTotalForDate(LocalDate date) {
        List<StripeBalanceTransaction> transactions = getBalanceTransactionsForDate(date);
        return transactions.stream()
                .map(StripeBalanceTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}