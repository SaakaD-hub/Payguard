package com.payguard.fraud.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudScoreRequest {
    
    @NotNull
    private UUID transactionId;
    
    @NotNull
    private UUID merchantId;
    
    @NotNull
    @Min(1)
    private Long amount;
    
    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;
    
    @NotBlank
    @Email
    private String customerEmail;
    
    @NotBlank
    private String timestamp;
}