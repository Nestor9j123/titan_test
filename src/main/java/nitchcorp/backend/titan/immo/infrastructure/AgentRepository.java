package nitchcorp.backend.titan.immo.infrastructure;

import nitchcorp.backend.titan.immo.domain.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    @Query("SELECT a FROM Agent a WHERE a.trackingId = :trackingId")
    Optional<Agent> getAgentByTrackingId(UUID trackingId);

    @Query("SELECT a FROM Agent a order by a.id DESC")
    List<Agent> getAllAgents();
    
    @Query("SELECT a FROM Agent a WHERE a.country = :country AND a.actif = true")
    List<Agent> findByCountryAndIsActiveTrue(@Param("country") String country);
}
