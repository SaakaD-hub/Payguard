package com.payguard.fraud.controller;

import com.payguard.fraud.dto.*;
import com.payguard.fraud.service.FraudScoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Fraud Controller - Trusts API Gateway
 * No authentication required (internal service)
 */
@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
@Tag(name = "Fraud Detection")
public class FraudController {

    private final FraudScoringService fraudScoringService;

    @PostMapping("/score")
    @Operation(summary = "Score transaction for fraud")
    public ResponseEntity<FraudScoreResponse> scoreFraud(
            @Valid @RequestBody FraudScoreRequest request
    ) {
        FraudScoreResponse response = fraudScoringService.scoreFraud(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Fraud Engine is healthy");
    }
}