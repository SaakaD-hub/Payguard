package consumer;

import com.payguard.notification.dto.EmailRequest;
import com.payguard.notification.service.EmailService;
import com.payguard.notification.service.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final EmailService emailService;
    private final NotificationTemplateService templateService;

    @KafkaListener(topics = "payment.completed", groupId = "notification-service")
    public void handlePaymentCompleted(Map<String, Object> eventData) {
        log.info("Received payment.completed event: {}", eventData.get("transactionId"));
        
        // Send email to merchant
        EmailRequest emailRequest = EmailRequest.builder()
                .to("merchant@example.com") // In production, fetch from merchant service
                .subject("Payment Completed - Transaction #" + eventData.get("transactionId"))
                .templateName("payment_completed")
                .templateVariables(templateService.buildPaymentCompletedVariables(eventData))
                .build();
        
        emailService.sendEmail(emailRequest);
    }

    @KafkaListener(topics = "payment.failed", groupId = "notification-service")
    public void handlePaymentFailed(Map<String, Object> eventData) {
        log.info("Received payment.failed event: {}", eventData.get("transactionId"));
        
        EmailRequest emailRequest = EmailRequest.builder()
                .to("merchant@example.com")
                .subject("Payment Failed - Transaction #" + eventData.get("transactionId"))
                .templateName("payment_failed")
                .templateVariables(templateService.buildPaymentFailedVariables(eventData))
                .build();
        
        emailService.sendEmail(emailRequest);
    }

    @KafkaListener(topics = "payment.blocked", groupId = "notification-service")
    public void handlePaymentBlocked(Map<String, Object> eventData) {
        log.info("Received payment.blocked event: {}", eventData.get("transactionId"));
        
        EmailRequest emailRequest = EmailRequest.builder()
                .to("merchant@example.com")
                .subject("ALERT: Payment Blocked - Transaction #" + eventData.get("transactionId"))
                .templateName("fraud_alert")
                .templateVariables(templateService.buildFraudAlertVariables(eventData))
                .build();
        
        emailService.sendEmail(emailRequest);
    }
}