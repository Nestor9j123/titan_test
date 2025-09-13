package nitchcorp.backend.titan.immo.infrastructure;

import nitchcorp.backend.titan.immo.domain.model.LeaseContrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface LeaseContractRepository extends JpaRepository<LeaseContrat, Long> {
    @Query("SELECT l FROM LeaseContrat l WHERE l.trackingId = :trackingId")
    Optional<LeaseContrat> getLeaseContractByTrackingId(UUID trackingId);

    @Query("SELECT l FROM LeaseContrat l  order by l.id DESC")
    List<LeaseContrat> getAllLeaseContracts();
}
