package com.payguard.payment.service;

import com.payguard.payment.dto.*;
import com.payguard.payment.exception.PaymentException;
import com.payguard.payment.model.*;
import com.payguard.payment.repository.*;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final RefundRepository refundRepository;
    private final WebhookEventRepository webhookEventRepository;
    private final StripeService stripeService;
    private final FraudCheckService fraudCheckService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public ChargeResponse processCharge(ChargeRequest request, UUID merchantId) {
        log.info("Processing charge for merchant: {}", merchantId);
        
        // Step 1: Create PENDING transaction
        Transaction transaction = Transaction.builder()
                .merchantId(merchantId)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .customerEmail(request.getCustomerEmail())
                .description(request.getDescription())
                .status(TransactionStatus.PENDING)
                .build();
        
        transaction = transactionRepository.save(transaction);
        log.info("Transaction created: {}", transaction.getId());
        
        // Step 2: Call Fraud Engine (SYNCHRONOUS)
        FraudScoreRequest fraudRequest = FraudScoreRequest.builder()
                .transactionId(transaction.getId())
                .merchantId(merchantId)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .customerEmail(request.getCustomerEmail())
                .timestamp(LocalDateTime.now().toString())
                .build();
        
        FraudScoreResponse fraudResponse = fraudCheckService.checkFraud(fraudRequest);
        
        // Update transaction with fraud score
        transaction.setFraudScore(fraudResponse.getFraudScore());
        transaction.setFraudDecision(fraudResponse.getDecision());
        
        // Step 3: Process based on fraud decision
        switch (fraudResponse.getDecision()) {
            case APPROVE -> {
                try {
                    // Charge via Stripe
                    Charge charge = stripeService.createCharge(
                            request.getAmount(),
                            request.getCurrency(),
                            request.getStripeToken(),
                            request.getDescription()
                    );
                    
                    transaction.setStripePaymentId(charge.getId());
                    transaction.setStatus(TransactionStatus.COMPLETED);
                    
                    // Publish event
                    publishPaymentEvent("payment.completed", transaction);
                    
                } catch (StripeException e) {
                    log.error("Stripe charge failed", e);
                    transaction.setStatus(TransactionStatus.FAILED);
                    publishPaymentEvent("payment.failed", transaction);
                    throw new PaymentException("Payment failed: " + e.getMessage());
                }
            }
            case REVIEW -> {
                transaction.setStatus(TransactionStatus.REVIEW);
                publishPaymentEvent("payment.review", transaction);
            }
            case BLOCK -> {
                transaction.setStatus(TransactionStatus.BLOCKED);
                publishPaymentEvent("payment.blocked", transaction);
            }
        }
        
        transaction = transactionRepository.save(transaction);
        
        return ChargeResponse.builder()
                .transactionId(transaction.getId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .status(transaction.getStatus())
                .fraudScore(transaction.getFraudScore())
                .fraudDecision(transaction.getFraudDecision())
                .stripeChargeId(transaction.getStripePaymentId())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    @Transactional
    public void processRefund(RefundRequest request, UUID merchantId) {
        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new PaymentException("Transaction not found"));
        
        if (!transaction.getMerchantId().equals(merchantId)) {
            throw new PaymentException("Unauthorized");
        }
        
        if (transaction.getStatus() != TransactionStatus.COMPLETED) {
            throw new PaymentException("Can only refund completed transactions");
        }
        
        try {
            com.stripe.model.Refund stripeRefund = stripeService.createRefund(
                    transaction.getStripePaymentId(),
                    request.getAmount(),
                    request.getReason()
            );
            
            Refund refund = Refund.builder()
                    .transactionId(transaction.getId())
                    .stripeRefundId(stripeRefund.getId())
                    .amount(request.getAmount())
                    .reason(request.getReason())
                    .status("COMPLETED")
                    .build();
            
            refundRepository.save(refund);
            log.info("Refund processed: {}", refund.getId());
            
        } catch (StripeException e) {
            log.error("Stripe refund failed", e);
            throw new PaymentException("Refund failed: " + e.getMessage());
        }
    }

    public List<Transaction> getTransactionsByMerchant(UUID merchantId) {
        return transactionRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId);
    }

    public Transaction getTransaction(UUID transactionId, UUID merchantId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new PaymentException("Transaction not found"));
        
        if (!transaction.getMerchantId().equals(merchantId)) {
            throw new PaymentException("Unauthorized");
        }
        
        return transaction;
    }

    @Transactional
    public void handleWebhook(String eventId, String eventType, String payload) {
        if (webhookEventRepository.existsByStripeEventId(eventId)) {
            log.info("Webhook already processed: {}", eventId);
            return;
        }
        
        WebhookEvent event = WebhookEvent.builder()
                .stripeEventId(eventId)
                .eventType(eventType)
                .payload(payload)
                .processed(true)
                .build();
        
        webhookEventRepository.save(event);
        log.info("Webhook processed: {}", eventId);
    }

    private void publishPaymentEvent(String topic, Transaction transaction) {
        Map<String, Object> event = Map.of(
                "transactionId", transaction.getId().toString(),
                "merchantId", transaction.getMerchantId().toString(),
                "amount", transaction.getAmount(),
                "currency", transaction.getCurrency(),
                "status", transaction.getStatus().toString(),
                "timestamp", transaction.getCreatedAt().toString()
        );
        
        kafkaTemplate.send(topic, transaction.getId().toString(), event);
        log.debug("Published event to {}: {}", topic, transaction.getId());
    }
}