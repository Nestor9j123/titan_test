package nitchcorp.backend.titan.food.Infrastructure;

import nitchcorp.backend.titan.food.Domain.models.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository  extends JpaRepository<Restaurant,Long> {

    @Query("SELECT r FROM Restaurant r WHERE r.trackingId = :trackingId")
    Optional<Restaurant> findByTrackingId(@Param("trackingId") UUID trackingId);

    @Query("select  r from Restaurant r order by r.id desc ")
    List<Restaurant> findAllRestaurants();
}
