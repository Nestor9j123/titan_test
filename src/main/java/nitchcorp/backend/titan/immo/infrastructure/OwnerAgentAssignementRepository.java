package nitchcorp.backend.titan.immo.infrastructure;

import nitchcorp.backend.titan.immo.domain.model.OwnerAgentAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OwnerAgentAssignementRepository extends JpaRepository<OwnerAgentAssignment, Long> {
    @Query("SELECT o FROM OwnerAgentAssignment o WHERE o.trackingId = :trackingId")
    Optional<OwnerAgentAssignment> getOwnerAgentAssignmentByTrackingId(UUID trackingId);

    @Query("SELECT o FROM OwnerAgentAssignment o  order by o.id DESC")
    List<OwnerAgentAssignment> getAllOwnerAgentAssignments();
    
    @Query("SELECT o FROM OwnerAgentAssignment o WHERE o.owner.trackingId = :ownerId")
    List<OwnerAgentAssignment> findByOwnerTrackingId(@Param("ownerId") UUID ownerId);
    
    @Query("SELECT o FROM OwnerAgentAssignment o WHERE o.agent.trackingId = :agentId")
    List<OwnerAgentAssignment> findByAgentTrackingId(@Param("agentId") UUID agentId);
    
    @Query("SELECT o FROM OwnerAgentAssignment o WHERE o.property.trackingId = :propertyId")
    Optional<OwnerAgentAssignment> findByPropertyTrackingId(@Param("propertyId") UUID propertyId);
}
