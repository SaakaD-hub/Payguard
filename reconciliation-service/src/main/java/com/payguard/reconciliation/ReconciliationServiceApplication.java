package com.payguard.reconciliation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Reconciliation Service - End-of-day settlement matching
 * Uses Spring Batch for reconciliation jobs
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableKafka
@EnableScheduling
public class ReconciliationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReconciliationServiceApplication.class, args);
    }
}