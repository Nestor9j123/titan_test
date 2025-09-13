package nitchcorp.backend.titan.events.infrastructure;

import nitchcorp.backend.titan.events.domain.model.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventsRepository extends JpaRepository<Events, Integer> {
    Optional<Object> getEventsByTrackingId(UUID trackingId) ;


    @Query("SELECT e FROM Events e WHERE e.trackingId = :trackingId")
    Optional<Events> findByTrackingId(@Param("trackingId") UUID trackingId);

    @Query("select  e from Events e order by e.id desc ")
    List<Events> findAllEvents();
}