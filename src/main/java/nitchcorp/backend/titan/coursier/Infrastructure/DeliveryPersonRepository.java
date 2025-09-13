package nitchcorp.backend.titan.coursier.Infrastructure;

import nitchcorp.backend.titan.coursier.Domain.models.DeliveryCompany;
import nitchcorp.backend.titan.coursier.Domain.models.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Integer> {
    Optional<DeliveryPerson> findByTrackingId(UUID trackingId);
    List<DeliveryPerson> findByDeliveryCompany(DeliveryCompany company);

    @Query("SELECT dp FROM DeliveryPerson dp WHERE dp.deliveryCompany = :company AND dp.isAvailable = true AND dp.isActive = true")
    List<DeliveryPerson> findAvailableByCompany(@Param("company") DeliveryCompany company);

    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
}
