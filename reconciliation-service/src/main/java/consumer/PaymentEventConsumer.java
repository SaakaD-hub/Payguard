package consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Payment Event Consumer
 * Tracks completed payments for reconciliation
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    @KafkaListener(topics = "payment.completed", groupId = "reconciliation-service")
    public void handlePaymentCompleted(Map<String, Object> eventData) {
        log.debug("Received payment.completed event: {}", eventData.get("transactionId"));
        
        // In production, store transaction data for daily reconciliation
        // For now, just log
    }
}