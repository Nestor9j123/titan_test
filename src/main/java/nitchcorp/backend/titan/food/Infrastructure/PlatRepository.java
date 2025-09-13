package nitchcorp.backend.titan.food.Infrastructure;

import nitchcorp.backend.titan.food.Domain.models.Plat;
import nitchcorp.backend.titan.food.Domain.enums.CategoriePlat;
import nitchcorp.backend.titan.food.Domain.models.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlatRepository extends JpaRepository<Plat,Long> {

    @Query("SELECT p FROM Plat p WHERE p.trackingId = :trackingId")
    Optional<Plat> findByTrackingId(@Param("trackingId") UUID trackingId);

    @Query("SELECT p FROM Plat p WHERE p.categorie = :categorie ORDER BY p.id DESC")
    List<Plat> findByCategoriePlat(@Param("categorie") CategoriePlat categorie);

    @Query("SELECT p.restaurant FROM Plat p WHERE p.trackingId = :trackingId")
    Optional<Restaurant> findRestaurantByPlatTrackingId(@Param("trackingId") UUID platTrackingId);

}
