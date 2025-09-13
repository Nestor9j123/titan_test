package nitchcorp.backend.titan.immo.infrastructure;

import nitchcorp.backend.titan.immo.domain.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    @Query("SELECT o FROM Owner o WHERE o.trackingId = :trackingId")
    Optional<Owner> getOwnerByTrackingId(UUID trackingId);

    @Query("SELECT o FROM Owner o order by o.id DESC")
    List<Owner> getAllOwners();
}
