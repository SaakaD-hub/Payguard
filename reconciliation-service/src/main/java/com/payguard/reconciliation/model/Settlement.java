package com.payguard.reconciliation.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "settlements")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settlement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private LocalDate settlementDate;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal internalTotal;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal stripeTotal;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal difference;
    
    @Column(nullable = false)
    private Integer transactionCount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReconciliationStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}