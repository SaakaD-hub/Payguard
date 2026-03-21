package com.payguard.payment.service;

import com.payguard.payment.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Idempotency Service - Prevents duplicate webhook processing
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

    private final WebhookEventRepository webhookEventRepository;

    public boolean isEventProcessed(String eventId) {
        boolean processed = webhookEventRepository.existsByStripeEventId(eventId);
        if (processed) {
            log.info("Event already processed: {}", eventId);
        }
        return processed;
    }
}