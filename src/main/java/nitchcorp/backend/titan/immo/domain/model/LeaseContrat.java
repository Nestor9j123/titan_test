package nitchcorp.backend.titan.immo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nitchcorp.backend.titan.immo.domain.enums.LeaseStatus;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.utils.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "LEASE_CONTRAT")
@Getter
@Setter
public class LeaseContrat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer; // Utilisateur avec rôle CUSTOMER

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agent; // Utilisateur avec rôle AGENT

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal rentAmount;
    private BigDecimal depositAmount;
    private String contractDocument;
    @Enumerated(EnumType.STRING)
    private LeaseStatus status;

    private UUID trackingId;

    private boolean autoPaymentEnabled;
}
