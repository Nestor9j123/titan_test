package nitchcorp.backend.titan.events.infrastructure;

import nitchcorp.backend.titan.events.domain.model.PurchasedTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchasedTicketRepository extends JpaRepository<PurchasedTicket, Long> {
    Optional<PurchasedTicket> findByTicketTrackingId(UUID ticketTrackingId);

    Optional<PurchasedTicket> findByVoucherCode(String voucherCode);

    List<PurchasedTicket> findAllByTicketTemplateTrackingId(UUID templateTrackingId);

    List<PurchasedTicket> findAllByTicketTemplateEventTrackingId(UUID eventTrackingId);

    List<PurchasedTicket> findAllByBuyerTrackingId(UUID buyerTrackingId);
}
