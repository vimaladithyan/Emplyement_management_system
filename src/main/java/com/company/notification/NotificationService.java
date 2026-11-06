package com.company.notification;

/**
 * Common notification interface (PRD §4.6) — the service layer depends only
 * on this abstraction, not on email/SMS specifics.
 */
public interface NotificationService {
    void sendNotification(String message);
}
