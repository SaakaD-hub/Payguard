package com.payguard.payment.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ChargeRequest {
    
    @NotNull
    @Min(value = 50, message = "Amount must be at least 50 cents")
    private Long amount;
    
    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;
    
    @NotBlank
    @Email
    private String customerEmail;
    
    @NotBlank
    private String stripeToken;
    
    private String description;
}