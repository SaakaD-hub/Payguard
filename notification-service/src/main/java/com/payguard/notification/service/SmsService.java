package com.payguard.notification.service;

import com.payguard.notification.dto.SmsRequest;
import com.payguard.notification.model.*;
import com.payguard.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SMS Service - Placeholder implementation
 * In production, integrate with Twilio, AWS SNS, or similar
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    private final NotificationLogRepository notificationLogRepository;

    public void sendSms(SmsRequest request) {
        log.info("Sending SMS to: {}", request.getPhoneNumber());
        
        try {
            // TODO: Integrate with SMS provider (Twilio, AWS SNS, etc.)
            // For now, just log
            log.info("SMS would be sent to {} with message: {}", 
                    request.getPhoneNumber(), request.getMessage());
            
            // Log success
            saveNotificationLog(request, NotificationStatus.SENT, null);
            
        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", request.getPhoneNumber(), e);
            saveNotificationLog(request, NotificationStatus.FAILED, e.getMessage());
        }
    }

    private void saveNotificationLog(SmsRequest request, 
                                     NotificationStatus status, 
                                     String errorMessage) {
        NotificationLog log = NotificationLog.builder()
                .type(NotificationType.SMS)
                .recipient(request.getPhoneNumber())
                .subject("SMS")
                .content(request.getMessage())
                .status(status)
                .errorMessage(errorMessage)
                .build();
        
        notificationLogRepository.save(log);
    }
}