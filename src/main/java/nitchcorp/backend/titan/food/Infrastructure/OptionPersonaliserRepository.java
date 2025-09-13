package nitchcorp.backend.titan.food.Infrastructure;

import nitchcorp.backend.titan.food.Domain.models.OptionPersonaliser;
import nitchcorp.backend.titan.food.Domain.models.Plat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OptionPersonaliserRepository extends JpaRepository<OptionPersonaliser , Long> {

    @Query("SELECT o FROM OptionPersonaliser o WHERE o.trackingId = :trackingId")
    Optional<OptionPersonaliser> findByTrackingId(@Param("trackingId") UUID trackingId);

    @Query("SELECT o FROM OptionPersonaliser o WHERE o.plat = :plat")
    Optional<OptionPersonaliser> findByPlat(@Param("plat") Plat plat);
}

