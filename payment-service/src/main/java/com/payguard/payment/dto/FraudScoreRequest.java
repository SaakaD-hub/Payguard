package com.payguard.payment.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
public class FraudScoreRequest {
    private UUID transactionId;
    private UUID merchantId;
    private Long amount;
    private String currency;
    private String customerEmail;
    private String timestamp;
}