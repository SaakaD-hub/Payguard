package com.payguard.fraud.exception;

public class FraudEngineException extends RuntimeException {
    public FraudEngineException(String message) {
        super(message);
    }
    
    public FraudEngineException(String message, Throwable cause) {
        super(message, cause);
    }
}