package com.payguard.payment.controller;

import com.payguard.payment.dto.*;
import com.payguard.payment.model.Transaction;
import com.payguard.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Payment Controller - Trusts API Gateway
 * Reads merchant context from X-User-Id header
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/charge")
    @Operation(summary = "Create a payment charge")
    public ResponseEntity<ChargeResponse> createCharge(
            @RequestHeader("X-User-Id") UUID merchantId,
            @Valid @RequestBody ChargeRequest request
    ) {
        ChargeResponse response = paymentService.processCharge(request, merchantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refund")
    @Operation(summary = "Refund a payment")
    public ResponseEntity<Void> refundPayment(
            @RequestHeader("X-User-Id") UUID merchantId,
            @Valid @RequestBody RefundRequest request
    ) {
        paymentService.processRefund(request, merchantId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "List merchant transactions")
    public ResponseEntity<List<Transaction>> listTransactions(
            @RequestHeader("X-User-Id") UUID merchantId
    ) {
        List<Transaction> transactions = paymentService.getTransactionsByMerchant(merchantId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction details")
    public ResponseEntity<Transaction> getTransaction(
            @RequestHeader("X-User-Id") UUID merchantId,
            @PathVariable UUID id
    ) {
        Transaction transaction = paymentService.getTransaction(id, merchantId);
        return ResponseEntity.ok(transaction);
    }
}