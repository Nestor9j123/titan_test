package nitchcorp.backend.titan.events.domain.model;

import jakarta.persistence.*;
import lombok.*;
import nitchcorp.backend.titan.events.domain.enums.TicketStatus;
import nitchcorp.backend.titan.shared.utils.BaseEntity;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import org.springframework.modulith.NamedInterface;

import java.util.UUID;

@NamedInterface
@Entity
@Table(name = "PURCHASED_TICKETS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PurchasedTicket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true,updatable = false)
    private UUID ticketTrackingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    private int numberOfTicketsBought;

    @Column(length = 500)
    private String qrCodeUrl;

    @Column(unique = true, length = 50)
    private String voucherCode;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TicketStatus status = TicketStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_template_id", nullable = false)
    private TicketTemplate ticketTemplate;
}