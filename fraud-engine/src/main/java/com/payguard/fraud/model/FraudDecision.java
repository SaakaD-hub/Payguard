package com.payguard.fraud.model;

public enum FraudDecision {
    APPROVE,   // Score: 0.0 - 0.29
    REVIEW,    // Score: 0.3 - 0.69
    BLOCK      // Score: 0.7 - 1.0
}