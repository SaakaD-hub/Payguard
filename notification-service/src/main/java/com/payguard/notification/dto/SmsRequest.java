package com.payguard.notification.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequest {
    private String phoneNumber;
    private String message;
}