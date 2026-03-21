package com.payguard.payment.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class RefundRequest {
    
    @NotNull
    private UUID transactionId;
    
    @NotNull
    @Min(1)
    private Long amount;
    
    private String reason;
}