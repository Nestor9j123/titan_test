package nitchcorp.backend.titan.immo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nitchcorp.backend.titan.immo.domain.enums.NotificationType;
import nitchcorp.backend.titan.immo.domain.enums.RecipientType;
import nitchcorp.backend.titan.shared.utils.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "NOTIFICATIONS")
@Getter
@Setter
public class Notification extends  BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recipientId; // ID de l'utilisateur (owner, agent, customer)
    @Enumerated(EnumType.STRING)
    private RecipientType recipientType; // Type de destinataire
    private String message;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private boolean isRead;
    private UUID trackingId;
}
