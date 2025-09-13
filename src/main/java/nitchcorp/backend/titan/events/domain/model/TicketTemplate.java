package nitchcorp.backend.titan.events.domain.model;


import jakarta.persistence.*;
import lombok.*;
import nitchcorp.backend.titan.events.domain.enums.TypeTicket;
import nitchcorp.backend.titan.shared.utils.BaseEntity;
import nitchcorp.backend.titan.shared.securite.user.entities.User;

import java.util.UUID;

@Entity
@Table(name = "TICKET_TEMPLATES")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TicketTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true,updatable = false)
    private UUID trackingId;

    @Enumerated(EnumType.STRING)
    private TypeTicket type;

    private double price;

    private int numberOfAvailableTickets;


    @Builder.Default
    private int numberOfTicketsSold = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Events event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    public boolean isAvailable() {
        return numberOfAvailableTickets == -1 || numberOfTicketsSold < numberOfAvailableTickets;
    }

    public int getNombreRestant() {
        if (numberOfAvailableTickets == -1) return -1;
        return Math.max(0, numberOfAvailableTickets - numberOfTicketsSold);
    }
}