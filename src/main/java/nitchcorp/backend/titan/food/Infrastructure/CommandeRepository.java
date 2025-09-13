package nitchcorp.backend.titan.food.Infrastructure;

import nitchcorp.backend.titan.food.Domain.models.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CommandeRepository extends JpaRepository<Commande,Long> {

    @Query("SELECT c FROM Commande c WHERE c.trackingId = :trackingId")
    Optional<Commande> findByTrackingId(@Param("trackingId") UUID trackingId);

}
