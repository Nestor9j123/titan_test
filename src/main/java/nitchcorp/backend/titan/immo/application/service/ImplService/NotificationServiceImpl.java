package nitchcorp.backend.titan.immo.application.service.ImplService;

import lombok.RequiredArgsConstructor;
import nitchcorp.backend.titan.immo.application.dto.requests.NotificationRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.NotificationResponse;
import nitchcorp.backend.titan.immo.application.mapper.NotificationMapper;
import nitchcorp.backend.titan.immo.domain.model.Notification;
import nitchcorp.backend.titan.immo.domain.exceptions.NotificationNotFoundException;
import nitchcorp.backend.titan.immo.infrastructure.NotificationRepository;
import nitchcorp.backend.titan.immo.application.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = notificationMapper.toEntity(request);
        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toResponse(savedNotification);
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {
        List<Notification> notifications = notificationRepository.getAllNotifications();
        return notifications.stream().map(notificationMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationResponse updateNotification(UUID trackingId, NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("NotificationRequest cannot be null");
        }
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        Notification existingNotification = notificationRepository.getNotificationByTrackingId(trackingId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with trackingId: " + trackingId));

        existingNotification.setRecipientId(request.recipientId());
        existingNotification.setRecipientType(request.recipientType());
        existingNotification.setMessage(request.message());
        existingNotification.setType(request.type());
        existingNotification.setRead(request.isRead());

        Notification updatedNotification = notificationRepository.save(existingNotification);
        return notificationMapper.toResponse(updatedNotification);
    }

    @Override
    public void deleteNotification(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        Notification existingNotification = notificationRepository.getNotificationByTrackingId(trackingId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with trackingId: " + trackingId));

        notificationRepository.delete(existingNotification);
    }

    @Override
    public NotificationResponse getNotificationByTrackingId(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        Notification notification = notificationRepository.getNotificationByTrackingId(trackingId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with trackingId: " + trackingId));

        return notificationMapper.toResponse(notification);
    }

    @Override
    public List<NotificationResponse> getNotificationsByRecipient(UUID recipientId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<NotificationResponse> getNotificationsByType(String type) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(UUID recipientId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public NotificationResponse markAsRead(UUID trackingId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int markAllAsRead(UUID recipientId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<NotificationResponse> getNotificationsByPriority(String priority) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int sendPushNotification(List<UUID> recipientIds, String title, String message, String priority) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int deleteReadNotifications(UUID recipientId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}