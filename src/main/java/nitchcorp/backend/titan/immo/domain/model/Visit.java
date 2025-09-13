package nitchcorp.backend.titan.immo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nitchcorp.backend.titan.immo.domain.enums.VisitStatus;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.utils.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "VISIT")
@Getter
@Setter
public class Visit  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agent; // Utilisateur avec rôle AGENT

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer; // Utilisateur avec rôle CUSTOMER

    private LocalDateTime visitDate;
    @Enumerated(EnumType.STRING)
    private VisitStatus status;
    private String comments;
    private UUID trackingId;
}
