package com.payguard.notification.dto;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    private String to;
    private String subject;
    private String templateName;
    private Map<String, Object> templateVariables;
}