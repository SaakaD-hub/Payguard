package com.payguard.user.dto;

import com.payguard.user.model.MerchantCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "Password must contain uppercase, lowercase, and number")
    private String password;
    
    @NotBlank(message = "Merchant name is required")
    private String merchantName;
    
    @NotNull(message = "Merchant category is required")
    private MerchantCategory merchantCategory;
    
    @NotBlank(message = "Country is required")
    @Size(min = 3, max = 3, message = "Country must be ISO 3166 alpha-3 code")
    private String country;
}