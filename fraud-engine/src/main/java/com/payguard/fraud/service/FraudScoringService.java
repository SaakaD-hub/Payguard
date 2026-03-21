package com.payguard.fraud.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payguard.fraud.dto.*;
import com.payguard.fraud.model.*;
import com.payguard.fraud.repository.FraudAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudScoringService {

    private final FeatureExtractionService featureExtractionService;
    private final ModelInferenceService modelInferenceService;
    private final RuleBasedFallbackService fallbackService;
    private final FraudAuditLogRepository auditLogRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public FraudScoreResponse scoreFraud(FraudScoreRequest request) {
        long startTime = System.currentTimeMillis();
        
        log.info("Scoring fraud for transaction: {}", request.getTransactionId());
        
        // Step 1: Extract features
        FeatureVector features = featureExtractionService.extractFeatures(request);
        
        // Step 2: Run ML model inference
        float rawScore = modelInferenceService.predict(features);
        BigDecimal fraudScore = BigDecimal.valueOf(rawScore)
                .setScale(4, RoundingMode.HALF_UP);
        
        // Step 3: Determine decision based on thresholds
        FraudDecision decision = determineDecision(fraudScore);
        
        // Step 4: Get contributing factors
        List<String> factors = getContributingFactors(features, fraudScore);
        
        int latencyMs = (int) (System.currentTimeMillis() - startTime);
        
        // Step 5: Store audit log
        saveAuditLog(request, features, fraudScore, decision, factors, latencyMs);
        
        // Step 6: Publish Kafka event
        publishFraudScoredEvent(request, fraudScore, decision);
        
        log.info("Fraud scoring completed. Score: {}, Decision: {}, Latency: {}ms", 
                fraudScore, decision, latencyMs);
        
        return FraudScoreResponse.builder()
                .fraudScore(fraudScore)
                .decision(decision)
                .contributingFactors(factors)
                .latencyMs(latencyMs)
                .build();
    }

    private FraudDecision determineDecision(BigDecimal fraudScore) {
        double score = fraudScore.doubleValue();
        
        if (score < 0.30) {
            return FraudDecision.APPROVE;
        } else if (score < 0.70) {
            return FraudDecision.REVIEW;
        } else {
            return FraudDecision.BLOCK;
        }
    }

    private List<String> getContributingFactors(FeatureVector features, BigDecimal score) {
        List<String> factors = fallbackService.getContributingFactors(features);
        
        // Add score-based factors
        if (score.doubleValue() > 0.8) {
            factors.add("very_high_fraud_score");
        } else if (score.doubleValue() > 0.5) {
            factors.add("high_fraud_score");
        }
        
        return factors;
    }

    private void saveAuditLog(FraudScoreRequest request, FeatureVector features, 
                             BigDecimal fraudScore, FraudDecision decision, 
                             List<String> factors, int latencyMs) {
        try {
            String featureJson = objectMapper.writeValueAsString(features);
            String factorsJson = objectMapper.writeValueAsString(factors);
            
            FraudAuditLog auditLog = FraudAuditLog.builder()
                    .transactionId(request.getTransactionId())
                    .merchantId(request.getMerchantId())
                    .fraudScore(fraudScore)
                    .decision(decision)
                    .featureVector(featureJson)
                    .contributingFactors(factorsJson)
                    .latencyMs(latencyMs)
                    .modelVersion(modelInferenceService.getModelVersion())
                    .build();
            
            auditLogRepository.save(auditLog);
            log.debug("Fraud audit log saved: {}", auditLog.getId());
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit log data", e);
        }
    }

    private void publishFraudScoredEvent(FraudScoreRequest request, 
                                        BigDecimal fraudScore, 
                                        FraudDecision decision) {
        Map<String, Object> event = Map.of(
                "transactionId", request.getTransactionId().toString(),
                "merchantId", request.getMerchantId().toString(),
                "fraudScore", fraudScore.doubleValue(),
                "decision", decision.toString(),
                "timestamp", request.getTimestamp()
        );
        
        kafkaTemplate.send("fraud.scored", request.getTransactionId().toString(), event);
        log.debug("Published fraud.scored event for transaction: {}", request.getTransactionId());
    }
}