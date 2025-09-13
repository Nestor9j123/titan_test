package nitchcorp.backend.titan.immo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.utils.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "OWNER_AGENT_ASSIGNMENT")
@Getter
@Setter
public class OwnerAgentAssignment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agent;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    private LocalDateTime assignedAt;

    private UUID trackingId;
    private String instructions;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "deactivation_reason")
    private String deactivationReason;
    
    @Column(name = "transfer_reason")
    private String transferReason;
}
