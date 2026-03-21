package com.payguard.reconciliation.service;

import com.payguard.reconciliation.model.Discrepancy;
import com.payguard.reconciliation.repository.DiscrepancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscrepancyService {

    private final DiscrepancyRepository discrepancyRepository;

    @Transactional
    public void recordDiscrepancy(UUID settlementId, 
                                  UUID transactionId,
                                  BigDecimal internalAmount,
                                  BigDecimal stripeAmount,
                                  String type,
                                  String description) {
        BigDecimal difference = internalAmount.subtract(
                stripeAmount != null ? stripeAmount : BigDecimal.ZERO
        );
        
        Discrepancy discrepancy = Discrepancy.builder()
                .settlementId(settlementId)
                .transactionId(transactionId)
                .internalAmount(internalAmount)
                .stripeAmount(stripeAmount)
                .difference(difference)
                .discrepancyType(type)
                .description(description)
                .resolved(false)
                .build();
        
        discrepancyRepository.save(discrepancy);
        log.warn("Discrepancy recorded for transaction {}: {} ({})", 
                transactionId, difference, type);
    }
}