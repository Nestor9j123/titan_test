package nitchcorp.backend.titan.events.infrastructure;

import nitchcorp.backend.titan.events.domain.model.TicketTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketTemplateRepository extends JpaRepository<TicketTemplate, Long> {
    Optional<TicketTemplate> findByTrackingId(UUID trackingId);

    List<TicketTemplate> findAllByEventTrackingId(UUID eventTrackingId);

    void deleteByTrackingId(UUID trackingId);

  @Query(value= """
            SELECT tt.* FROM ticket_templates tt
            ORDER BY tt.id DESC ; 
            """ , nativeQuery = true)
    List<TicketTemplate> getAll() ;
}
