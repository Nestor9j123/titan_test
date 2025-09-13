package nitchcorp.backend.titan.events.infrastructure;

import nitchcorp.backend.titan.events.domain.model.Votes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VotesRepository extends JpaRepository<Votes, Long> {

    @Query("SELECT v FROM Votes v WHERE v.trackingId = :trackingId")
    Optional<Votes> getVotesByTrackingId(UUID trackingId);

    @Query("SELECT v FROM Votes v  order by v.id DESC")
    List<Votes> getAllVotes();

    @Query(value = """
            SELECT v.* FROM votes v 
            JOIN events e ON e.id = v.event_id
            WHERE v.event_id = :id_event 
            ORDER BY v.id DESC
            """ , nativeQuery = true)

    List<Votes> getAllVotesByTrackingIdEvent(@Param("id_event") Long id_event) ;


}
