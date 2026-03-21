package com.payguard.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Notification Service - Email & SMS notifications
 * Consumes Kafka events (payment.completed, fraud.scored, user.registered)
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableKafka
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}