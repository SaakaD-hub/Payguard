package com.payguard.reconciliation.service;

import com.payguard.reconciliation.dto.ReconciliationReport;
import com.payguard.reconciliation.model.*;
import com.payguard.reconciliation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReconciliationService {

    private final SettlementRepository settlementRepository;
    private final DiscrepancyRepository discrepancyRepository;
    private final StripeReportService stripeReportService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public ReconciliationReport reconcileDay(LocalDate date, 
                                            BigDecimal internalTotal, 
                                            int transactionCount) {
        log.info("Starting reconciliation for date: {}", date);
        
        // Fetch Stripe totals
        BigDecimal stripeTotal = stripeReportService.getTotalForDate(date);
        
        // Calculate difference
        BigDecimal difference = internalTotal.subtract(stripeTotal);
        
        // Determine status
        ReconciliationStatus status = difference.abs().compareTo(BigDecimal.valueOf(0.01)) < 0
                ? ReconciliationStatus.MATCHED
                : ReconciliationStatus.DISCREPANCY;
        
        // Create settlement record
        Settlement settlement = Settlement.builder()
                .settlementDate(date)
                .internalTotal(internalTotal)
                .stripeTotal(stripeTotal)
                .difference(difference)
                .transactionCount(transactionCount)
                .status(status)
                .build();
        
        settlement = settlementRepository.save(settlement);
        log.info("Settlement created for {}: {} (status: {})", date, settlement.getId(), status);
        
        // Publish event
        publishReconciliationEvent(settlement);
        
        // Build report
        int discrepancyCount = discrepancyRepository.findBySettlementId(settlement.getId()).size();
        
        return ReconciliationReport.builder()
                .settlementDate(date)
                .internalTotal(internalTotal)
                .stripeTotal(stripeTotal)
                .difference(difference)
                .transactionCount(transactionCount)
                .discrepancyCount(discrepancyCount)
                .status(status.toString())
                .build();
    }

    private void publishReconciliationEvent(Settlement settlement) {
        Map<String, Object> event = Map.of(
                "settlementId", settlement.getId().toString(),
                "settlementDate", settlement.getSettlementDate().toString(),
                "status", settlement.getStatus().toString(),
                "difference", settlement.getDifference().doubleValue()
        );
        
        kafkaTemplate.send("reconciliation.completed", 
                settlement.getId().toString(), event);
        log.debug("Published reconciliation.completed event");
    }
}