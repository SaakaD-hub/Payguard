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
public class FraudEventConsumer {

    private final EmailService emailService;
    private final NotificationTemplateService templateService;

    @KafkaListener(topics = "fraud.scored", groupId = "notification-service")
    public void handleFraudScored(Map<String, Object> eventData) {
        log.info("Received fraud.scored event: {}", eventData.get("transactionId"));
        
        String decision = (String) eventData.get("decision");
        
        // Only send alerts for BLOCK or high-risk REVIEW decisions
        if ("BLOCK".equals(decision)) {
            EmailRequest emailRequest = EmailRequest.builder()
                    .to("admin@payguard.com")
                    .subject("FRAUD ALERT: Transaction Blocked - " + eventData.get("transactionId"))
                    .templateName("fraud_alert")
                    .templateVariables(templateService.buildFraudAlertVariables(eventData))
                    .build();
            
            emailService.sendEmail(emailRequest);
        }
    }
}