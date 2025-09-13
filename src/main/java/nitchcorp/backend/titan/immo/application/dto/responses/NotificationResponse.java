package nitchcorp.backend.titan.immo.application.dto.responses;

import nitchcorp.backend.titan.immo.domain.enums.NotificationType;
import nitchcorp.backend.titan.immo.domain.enums.RecipientType;

import java.util.UUID;
public record NotificationResponse(
        UUID trackingId,
        Long id,
        Long recipientId,
        RecipientType recipientType,
        String message,
        NotificationType type,
        boolean isRead
) {}