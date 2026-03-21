package com.payguard.fraud.service;

import com.payguard.fraud.dto.FeatureVector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Rule-Based Fallback Service
 * Used when ML model is unavailable
 */
@Service
@Slf4j
public class RuleBasedFallbackService {

    public BigDecimal calculateFraudScore(FeatureVector features) {
        log.info("Using rule-based fraud scoring");
        
        double score = 0.0;
        
        // Rule 1: Large transaction amount
        if (features.getTransactionAmount() > 5.0) {
            score += 0.3;
        }
        
        // Rule 2: Off-hours transaction
        if (features.getIsOffHours() == 1) {
            score += 0.15;
        }
        
        // Rule 3: High merchant activity (potential fraud)
        if (features.getMerchantTxCountLast24h() > 50) {
            score += 0.2;
        }
        
        // Rule 4: Large deviation from average
        if (Math.abs(features.getAmountDeviationFromAvg()) > 3.0) {
            score += 0.25;
        }
        
        // Rule 5: First-time customer
        if (features.getCustomerTxCountLast24h() == 0) {
            score += 0.1;
        }
        
        // Normalize to 0-1 range
        score = Math.min(1.0, score);
        
        return BigDecimal.valueOf(score);
    }

    public List<String> getContributingFactors(FeatureVector features) {
        List<String> factors = new ArrayList<>();
        
        if (features.getTransactionAmount() > 5.0) {
            factors.add("high_transaction_amount");
        }
        if (features.getIsOffHours() == 1) {
            factors.add("off_hours_transaction");
        }
        if (features.getMerchantTxCountLast24h() > 50) {
            factors.add("high_merchant_activity");
        }
        if (Math.abs(features.getAmountDeviationFromAvg()) > 3.0) {
            factors.add("amount_deviation");
        }
        if (features.getCustomerTxCountLast24h() == 0) {
            factors.add("first_time_customer");
        }
        
        return factors;
    }
}