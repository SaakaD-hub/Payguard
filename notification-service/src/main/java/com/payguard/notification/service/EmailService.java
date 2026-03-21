package com.payguard.notification.service;

import com.payguard.notification.dto.EmailRequest;
import com.payguard.notification.model.*;
import com.payguard.notification.repository.NotificationLogRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final NotificationLogRepository notificationLogRepository;

    public void sendEmail(EmailRequest request) {
        log.info("Sending email to: {}", request.getTo());
        
        try {
            // Process template
            String htmlContent = processTemplate(request.getTemplateName(), 
                    request.getTemplateVariables());
            
            // Send email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@payguard.com");
            
            mailSender.send(message);
            
            // Log success
            saveNotificationLog(request, NotificationStatus.SENT, null);
            log.info("Email sent successfully to: {}", request.getTo());
            
        } catch (Exception e) {
            log.error("Failed to send email to: {}", request.getTo(), e);
            saveNotificationLog(request, NotificationStatus.FAILED, e.getMessage());
        }
    }

    private String processTemplate(String templateName, 
                                   java.util.Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }

    private void saveNotificationLog(EmailRequest request, 
                                     NotificationStatus status, 
                                     String errorMessage) {
        NotificationLog log = NotificationLog.builder()
                .type(NotificationType.EMAIL)
                .recipient(request.getTo())
                .subject(request.getSubject())
                .content(request.getTemplateName())
                .status(status)
                .errorMessage(errorMessage)
                .build();
        
        notificationLogRepository.save(log);
    }
}