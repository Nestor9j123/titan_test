package nitchcorp.backend.titan.immo.application.service;

import nitchcorp.backend.titan.immo.application.dto.requests.NotificationRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    NotificationResponse createNotification(NotificationRequest request);
    List<NotificationResponse> getAllNotifications();
    NotificationResponse updateNotification(UUID trackingId, NotificationRequest request);
    void deleteNotification(UUID trackingId);
    NotificationResponse getNotificationByTrackingId(UUID trackingId);
    
    // Nouvelles méthodes ajoutées pour les endpoints
    List<NotificationResponse> getNotificationsByRecipient(UUID recipientId);
    List<NotificationResponse> getNotificationsByType(String type);
    List<NotificationResponse> getUnreadNotifications(UUID recipientId);
    NotificationResponse markAsRead(UUID trackingId);
    int markAllAsRead(UUID recipientId);
    List<NotificationResponse> getNotificationsByPriority(String priority);
    int sendPushNotification(List<UUID> recipientIds, String title, String message, String priority);
    int deleteReadNotifications(UUID recipientId);
}