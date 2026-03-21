package com.payguard.payment.service;

import com.payguard.payment.dto.FraudScoreRequest;
import com.payguard.payment.dto.FraudScoreResponse;
import com.payguard.payment.model.FraudDecision;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

/**
 * Fraud Check Service - Calls Fraud Engine synchronously
 * Uses Circuit Breaker for resilience
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FraudCheckService {

    @Value("${fraud-engine.url}")
    private String fraudEngineUrl;

    private final RestClient restClient;

    @CircuitBreaker(name = "fraudEngine", fallbackMethod = "fraudCheckFallback")
    public FraudScoreResponse checkFraud(FraudScoreRequest request) {
        log.info("Calling Fraud Engine for transaction: {}", request.getTransactionId());
        
        FraudScoreResponse response = restClient.post()
                .uri(fraudEngineUrl + "/api/v1/fraud/score")
                .body(request)
                .retrieve()
                .body(FraudScoreResponse.class);
        
        log.info("Fraud check result: {} with score {}", 
                response.getDecision(), response.getFraudScore());
        
        return response;
    }

    /**
     * Fallback method when Fraud Engine is unavailable
     * Uses rule-based decision
     */
    public FraudScoreResponse fraudCheckFallback(FraudScoreRequest request, Exception ex) {
        log.warn("Fraud Engine unavailable, using rule-based fallback for transaction: {}", 
                request.getTransactionId(), ex);
        
        // Simple rule: amounts over $5000 go to review, others approve
        FraudDecision decision = request.getAmount() > 500000 
                ? FraudDecision.REVIEW 
                : FraudDecision.APPROVE;
        
        return FraudScoreResponse.builder()
                .fraudScore(BigDecimal.valueOf(0.1))
                .decision(decision)
                .contributingFactors(List.of("fallback_rule_based"))
                .latencyMs(0)
                .build();
    }
}