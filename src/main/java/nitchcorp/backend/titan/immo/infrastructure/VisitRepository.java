package nitchcorp.backend.titan.immo.infrastructure;

import nitchcorp.backend.titan.immo.domain.model.Visit;
import nitchcorp.backend.titan.immo.domain.enums.VisitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    @Query("SELECT s FROM Visit s WHERE s.trackingId = :trackingId")
    Optional<Visit> getVisitByTrackingId(UUID trackingId);

    @Query("SELECT v FROM Visit v ORDER BY v.id DESC")
    List<Visit> getAllVisits();

        @Query("SELECT v FROM Visit v WHERE v.agent.trackingId = :agentId")
        List<Visit> findByAgentTrackingId(@Param("agentId") UUID agentId);

        @Query("SELECT v FROM Visit v WHERE v.customer.trackingId = :customerId")
        List<Visit> findByCustomerTrackingId(@Param("customerId") UUID customerId);

        @Query("SELECT v FROM Visit v WHERE v.property.trackingId = :propertyId")
        List<Visit> findByPropertyTrackingId(@Param("propertyId") UUID propertyId);

    List<Visit> findByStatus(VisitStatus status);
}
