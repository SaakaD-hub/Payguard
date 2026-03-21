package com.payguard.fraud.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fraud_audit_log")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudAuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private UUID transactionId;
    
    @Column(nullable = false)
    private UUID merchantId;
    
    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal fraudScore;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudDecision decision;
    
    @Column(columnDefinition = "JSONB")
    private String featureVector;
    
    @Column(columnDefinition = "TEXT")
    private String contributingFactors;
    
    @Column(nullable = false)
    private Integer latencyMs;
    
    @Column(nullable = false)
    private String modelVersion;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}