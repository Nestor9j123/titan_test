package nitchcorp.backend.titan.immo.application.dto.requests;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import nitchcorp.backend.titan.immo.domain.enums.NotificationType;
import nitchcorp.backend.titan.immo.domain.enums.RecipientType;

@Builder
public record NotificationRequest(
       @JsonProperty("recipientId")
       @NotNull(message = "L'ID du destinataire est requis")
       Long recipientId,
       @JsonProperty("recipientType")
       @NotNull(message = "Le type de destinataire est requis")
       RecipientType recipientType,
       @JsonProperty("message")
       @NotNull(message = "Le message est requis")
       String message,
       @JsonProperty("type")
       @NotNull(message = "Le type de notification est requis")
       NotificationType type,
       @JsonProperty("isRead")
       boolean isRead
) {}
