package com.payguard.user.dto;

import lombok.*;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String tokenType;
    private Long expiresIn; // seconds
    private UserResponse user;
}