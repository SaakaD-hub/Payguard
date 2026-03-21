package com.payguard.fraud.service;

import com.payguard.fraud.dto.FeatureVector;
import com.payguard.fraud.dto.FraudScoreRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Feature Extraction Service
 * Extracts 12 features from transaction data and Redis feature store
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeatureExtractionService {

    private final RedisTemplate<String, String> redisTemplate;

    public FeatureVector extractFeatures(FraudScoreRequest request) {
        log.debug("Extracting features for transaction: {}", request.getTransactionId());
        
        LocalDateTime txTime = LocalDateTime.parse(request.getTimestamp(), 
                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        // Feature 1: Normalized transaction amount (amount / 100000)
        Double normalizedAmount = request.getAmount() / 100000.0;
        
        // Feature 2: Hour of day (0-23)
        Integer hourOfDay = txTime.getHour();
        
        // Feature 3: Day of week (0-6)
        Integer dayOfWeek = txTime.getDayOfWeek().getValue() - 1;
        
        // Feature 4-6: Redis counters (merchant & customer activity)
        String merchantKey = "merchant:" + request.getMerchantId() + ":tx_count_24h";
        String merchantVolumeKey = "merchant:" + request.getMerchantId() + ":volume_24h";
        String customerKey = "customer:" + request.getCustomerEmail() + ":tx_count_24h";
        
        Integer merchantTxCount = getRedisCounter(merchantKey);
        Double merchantVolume = getRedisVolume(merchantVolumeKey);
        Integer customerTxCount = getRedisCounter(customerKey);
        
        // Update Redis counters
        incrementCounter(merchantKey, 1);
        incrementCounter(merchantVolumeKey, request.getAmount());
        incrementCounter(customerKey, 1);
        
        // Feature 7: Amount deviation from merchant average
        Double avgAmount = merchantTxCount > 0 ? merchantVolume / merchantTxCount : 0.0;
        Double amountDeviation = avgAmount > 0 ? 
                (request.getAmount() - avgAmount) / avgAmount : 0.0;
        
        // Feature 8: Time since last transaction (mock - in real system query Redis)
        Integer timeSinceLastTx = 60; // Default 60 minutes
        
        // Feature 9: Cross-border transaction (simplified - always 0)
        Integer isCrossBorder = 0;
        
        // Feature 10: High-risk merchant category (simplified - always 0)
        Integer isHighRiskCategory = 0;
        
        // Feature 11: Weekend transaction
        Integer isWeekend = (txTime.getDayOfWeek() == DayOfWeek.SATURDAY || 
                             txTime.getDayOfWeek() == DayOfWeek.SUNDAY) ? 1 : 0;
        
        // Feature 12: Off-hours transaction (before 6 AM or after 10 PM)
        Integer isOffHours = (hourOfDay < 6 || hourOfDay > 22) ? 1 : 0;
        
        FeatureVector features = FeatureVector.builder()
                .transactionAmount(normalizedAmount)
                .hourOfDay(hourOfDay)
                .dayOfWeek(dayOfWeek)
                .merchantTxCountLast24h(merchantTxCount)
                .merchantVolumeLast24h(merchantVolume / 100.0) // Normalize
                .customerTxCountLast24h(customerTxCount)
                .amountDeviationFromAvg(amountDeviation)
                .timeSinceLastTx(timeSinceLastTx)
                .isCrossBorder(isCrossBorder)
                .isHighRiskCategory(isHighRiskCategory)
                .isWeekend(isWeekend)
                .isOffHours(isOffHours)
                .build();
        
        log.debug("Extracted features: {}", features);
        return features;
    }

    private Integer getRedisCounter(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.parseInt(value) : 0;
    }

    private Double getRedisVolume(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Double.parseDouble(value) : 0.0;
    }

    private void incrementCounter(String key, long delta) {
        redisTemplate.opsForValue().increment(key, delta);
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
    }
}