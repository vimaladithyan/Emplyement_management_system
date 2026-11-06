package com.company.notification;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Alternative NotificationService, selected explicitly with
 * {@code @Qualifier("smsNotificationService")} (PRD §4.6).
 * Console simulation only — no real SMS gateway.
 */
@Service
public class SmsNotificationService implements NotificationService {

    @Value("${notification.sms.sender:COMPANY}")
    private String sender;

    @PostConstruct
    public void init() {
        System.out.println("[SmsNotificationService] Initialised (@PostConstruct), sender=" + sender);
    }

    @PreDestroy
    public void destroy() {
        System.out.println("[SmsNotificationService] Destroyed (@PreDestroy)");
    }

    @Override
    public void sendNotification(String message) {
        System.out.println("[SMS sender=" + sender + "] " + message);
    }
}
