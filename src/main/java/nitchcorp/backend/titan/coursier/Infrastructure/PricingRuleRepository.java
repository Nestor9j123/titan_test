package nitchcorp.backend.titan.coursier.Infrastructure;

import nitchcorp.backend.titan.coursier.Domain.models.DeliveryCompany;
import nitchcorp.backend.titan.coursier.Domain.models.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PricingRuleRepository extends JpaRepository<PricingRule, Integer> {

    List<PricingRule> findByDeliveryCompany(DeliveryCompany company);

    @Query("SELECT pr FROM PricingRule pr WHERE pr.deliveryCompany = :company " +
            "AND pr.minDistanceKm <= :distance AND pr.maxDistanceKm >= :distance")
    Optional<PricingRule> findByDeliveryCompanyAndDistanceRange(
            @Param("company") DeliveryCompany company,
            @Param("distance") Double distance
    );

    Optional<PricingRule> findByTrackingId(UUID trackingId);

    List<PricingRule> findAllByDeliveryCompany(DeliveryCompany deliveryCompany);



}
