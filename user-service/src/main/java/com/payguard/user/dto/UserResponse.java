package com.payguard.user.dto;

import com.payguard.user.model.MerchantCategory;
import com.payguard.user.model.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String merchantName;
    private MerchantCategory merchantCategory;
    private String country;
    private Role role;
    private LocalDateTime createdAt;
}