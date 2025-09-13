package nitchcorp.backend.titan.coursier.Infrastructure;

import nitchcorp.backend.titan.coursier.Domain.models.DeliveryCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryCompanyRepository extends JpaRepository<DeliveryCompany, Integer> {
    Optional<DeliveryCompany> findByTrackingId(UUID trackingId);

    @Query("SELECT dc FROM DeliveryCompany dc WHERE dc.isActive = true")
    List<DeliveryCompany> findAllActive();

    boolean existsByName(String name);
    boolean existsByContactEmail(String contactEmail);

    void deleteByTrackingId(UUID trackingId);
}
