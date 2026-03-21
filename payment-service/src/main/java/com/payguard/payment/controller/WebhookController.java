package com.payguard.payment.controller;

import com.payguard.payment.service.IdempotencyService;
import com.payguard.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Webhook Controller - Handles Stripe webhooks
 * Implements idempotency to prevent duplicate processing
 */
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final PaymentService paymentService;
    private final IdempotencyService idempotencyService;

    @PostMapping("/stripe")
    public ResponseEntity<Void> handleStripeWebhook(@RequestBody Map<String, Object> payload) {
        String eventId = (String) payload.get("id");
        String eventType = (String) payload.get("type");
        
        log.info("Received Stripe webhook: {} ({})", eventId, eventType);
        
        // Check idempotency
        if (idempotencyService.isEventProcessed(eventId)) {
            log.info("Webhook already processed: {}", eventId);
            return ResponseEntity.ok().build();
        }
        
        // Process webhook
        paymentService.handleWebhook(eventId, eventType, payload.toString());
        
        return ResponseEntity.ok().build();
    }
}