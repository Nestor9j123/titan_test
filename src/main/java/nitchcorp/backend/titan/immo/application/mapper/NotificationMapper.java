package nitchcorp.backend.titan.immo.application.mapper;

import nitchcorp.backend.titan.immo.application.dto.requests.NotificationRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.NotificationResponse;
import nitchcorp.backend.titan.immo.domain.model.Notification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("Notification cannot be null");
        }

        return new NotificationResponse(
                notification.getTrackingId(),
                notification.getId(),
                notification.getRecipientId(),
                notification.getRecipientType(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead()
        );
    }

    public Notification toEntity(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("NotificationRequest cannot be null");
        }

        Notification notification = new Notification();
        notification.setTrackingId(UUID.randomUUID());
        notification.setRecipientId(request.recipientId());
        notification.setRecipientType(request.recipientType());
        notification.setMessage(request.message());
        notification.setType(request.type());
        notification.setRead(request.isRead());

        return notification;
    }

    public static Notification toEntityFromResponse(NotificationResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("NotificationResponse cannot be null");
        }

        Notification notification = new Notification();
        notification.setTrackingId(response.trackingId());
        notification.setId(response.id());
        notification.setRecipientId(response.recipientId());
        notification.setRecipientType(response.recipientType());
        notification.setMessage(response.message());
        notification.setType(response.type());
        notification.setRead(response.isRead());

        return notification;
    }
}