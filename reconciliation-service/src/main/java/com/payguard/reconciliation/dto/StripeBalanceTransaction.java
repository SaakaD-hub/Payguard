package com.payguard.reconciliation.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripeBalanceTransaction {
    private String id;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime created;
    private String type;
}