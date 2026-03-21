package com.payguard.reconciliation.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "discrepancies")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discrepancy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private UUID settlementId;
    
    @Column(nullable = false)
    private UUID transactionId;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal internalAmount;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal stripeAmount;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal difference;
    
    @Column(nullable = false)
    private String discrepancyType;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Boolean resolved;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}