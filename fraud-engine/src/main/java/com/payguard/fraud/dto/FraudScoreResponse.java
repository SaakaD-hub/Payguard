package com.payguard.fraud.dto;

import com.payguard.fraud.model.FraudDecision;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudScoreResponse {
    private BigDecimal fraudScore;
    private FraudDecision decision;
    private List<String> contributingFactors;
    private Integer latencyMs;
}