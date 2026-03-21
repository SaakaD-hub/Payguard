package com.payguard.payment.dto;

import com.payguard.payment.model.FraudDecision;
import com.payguard.payment.model.TransactionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ChargeResponse {
    private UUID transactionId;
    private Long amount;
    private String currency;
    private TransactionStatus status;
    private BigDecimal fraudScore;
    private FraudDecision fraudDecision;
    private String stripeChargeId;
    private LocalDateTime createdAt;
}