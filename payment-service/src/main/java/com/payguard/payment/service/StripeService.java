package com.payguard.payment.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Stripe Service - Handles Stripe API calls
 */
@Service
@Slf4j
public class StripeService {

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public Charge createCharge(Long amount, String currency, String token, String description) 
            throws StripeException {
        log.info("Creating Stripe charge for amount: {} {}", amount, currency);
        
        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setSource(token)
                .setDescription(description)
                .build();
        
        Charge charge = Charge.create(params);
        log.info("Stripe charge created: {}", charge.getId());
        
        return charge;
    }

    public com.stripe.model.Refund createRefund(String chargeId, Long amount, String reason) 
            throws StripeException {
        log.info("Creating Stripe refund for charge: {}", chargeId);
        
        com.stripe.param.RefundCreateParams params = 
                com.stripe.param.RefundCreateParams.builder()
                        .setCharge(chargeId)
                        .setAmount(amount)
                        .setReason(com.stripe.param.RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                        .build();
        
        com.stripe.model.Refund refund = com.stripe.model.Refund.create(params);
        log.info("Stripe refund created: {}", refund.getId());
        
        return refund;
    }
}