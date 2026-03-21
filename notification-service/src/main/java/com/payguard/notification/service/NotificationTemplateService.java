package com.payguard.notification.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationTemplateService {

    public Map<String, Object> buildPaymentCompletedVariables(Map<String, Object> eventData) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("transactionId", eventData.get("transactionId"));
        variables.put("amount", formatAmount((Long) eventData.get("amount")));
        variables.put("currency", eventData.get("currency"));
        variables.put("timestamp", eventData.get("timestamp"));
        return variables;
    }

    public Map<String, Object> buildPaymentFailedVariables(Map<String, Object> eventData) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("transactionId", eventData.get("transactionId"));
        variables.put("amount", formatAmount((Long) eventData.get("amount")));
        variables.put("reason", "Payment processing failed");
        return variables;
    }

    public Map<String, Object> buildFraudAlertVariables(Map<String, Object> eventData) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("transactionId", eventData.get("transactionId"));
        variables.put("fraudScore", eventData.get("fraudScore"));
        variables.put("decision", eventData.get("decision"));
        variables.put("timestamp", eventData.get("timestamp"));
        return variables;
    }

    public Map<String, Object> buildUserRegisteredVariables(Map<String, Object> eventData) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("email", eventData.get("email"));
        variables.put("merchantName", eventData.get("merchantName"));
        return variables;
    }

    private String formatAmount(Long amountInCents) {
        if (amountInCents == null) return "$0.00";
        double amount = amountInCents / 100.0;
        return String.format("$%.2f", amount);
    }
}