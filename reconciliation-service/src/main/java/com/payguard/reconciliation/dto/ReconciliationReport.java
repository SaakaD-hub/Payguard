package com.payguard.reconciliation.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationReport {
    private LocalDate settlementDate;
    private BigDecimal internalTotal;
    private BigDecimal stripeTotal;
    private BigDecimal difference;
    private Integer transactionCount;
    private Integer discrepancyCount;
    private String status;
}