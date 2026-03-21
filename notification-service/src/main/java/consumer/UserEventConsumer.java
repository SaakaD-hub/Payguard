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
public class UserEventConsumer {

    private final EmailService emailService;
    private final NotificationTemplateService templateService;

    @KafkaListener(topics = "user.registered", groupId = "notification-service")
    public void handleUserRegistered(Map<String, Object> eventData) {
        log.info("Received user.registered event: {}", eventData.get("userId"));
        
        String email = (String) eventData.get("email");
        
        EmailRequest emailRequest = EmailRequest.builder()
                .to(email)
                .subject("Welcome to PayGuard!")
                .templateName("user_registered")
                .templateVariables(templateService.buildUserRegisteredVariables(eventData))
                .build();
        
        emailService.sendEmail(emailRequest);
    }
}