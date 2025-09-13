package nitchcorp.backend.titan.immo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nitchcorp.backend.titan.immo.domain.enums.PaiementStatus;
import nitchcorp.backend.titan.immo.domain.enums.PaiementType;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.utils.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "PAIEMENT")
@Getter
@Setter
public class Paiement extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lease_contract_id")
    private LeaseContrat leaseContract;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer; // Utilisateur avec rôle CUSTOMER

    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PaiementType paymentType;
    private LocalDateTime paymentDate;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private PaiementStatus status;
    @Column(unique = true)
    private String transactionId;

    private UUID trackingId;
}
