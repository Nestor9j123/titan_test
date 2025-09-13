package nitchcorp.backend.titan.food.Infrastructure;

import nitchcorp.backend.titan.food.Domain.models.Plat;
import nitchcorp.backend.titan.food.Domain.models.PlatCommande;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlatCommandeRepository extends JpaRepository<PlatCommande,Long> {

    @Query("SELECT pc FROM PlatCommande pc WHERE pc.trackingId = :trackingId")
    Optional<PlatCommande> findByTrackingId(@Param("trackingId") UUID trackingId);


    @Query("SELECT p FROM PlatCommande p WHERE p.commande.trackingId = :trackingId ")
    List<PlatCommande> findAllByCommande(@Param("trackingId") UUID trackingId);

    @Query("SELECT pc.plat FROM PlatCommande pc " +
            "WHERE pc.commande.user.id = :userId " +
            "GROUP BY pc.plat " +
            "ORDER BY COUNT(pc.id) DESC")
    List<Plat> findTopPlatsByUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT pc.plat FROM PlatCommande pc " +
            "GROUP BY pc.plat " +
            "ORDER BY COUNT(pc.id) DESC")
    List<Plat> findTopPlatsGlobally(Pageable pageable);

    @Query("SELECT pc.plat FROM PlatCommande pc WHERE pc.trackingId = :trackingId")
    Optional<Plat> findPlatByPlatCommandeTrackingId(@Param("trackingId") UUID platCommandeTrackingId);
}
