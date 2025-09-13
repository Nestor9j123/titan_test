package nitchcorp.backend.titan.coursier.Infrastructure;

import nitchcorp.backend.titan.coursier.Domain.models.DeliveryOrder;
import nitchcorp.backend.titan.coursier.Domain.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Integer> {

    Optional<DeliveryOrder> findByTrackingId(UUID trackingId);

    List<DeliveryOrder> findByStatus(DeliveryStatus status);

    @Query("SELECT do FROM DeliveryOrder do WHERE do.assignedDeliveryPerson.trackingId = :deliveryPersonTrackingId")
    List<DeliveryOrder> findByAssignedDeliveryPerson(@Param("deliveryPersonTrackingId") UUID deliveryPersonTrackingId);

    @Query("SELECT do FROM DeliveryOrder do WHERE do.deliveryCompany.trackingId = :companyTrackingId")
    List<DeliveryOrder> findByDeliveryCompany(@Param("companyTrackingId") UUID companyTrackingId);
}
