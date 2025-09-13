package nitchcorp.backend.titan.immo.infrastructure;

import nitchcorp.backend.titan.immo.domain.model.Property;
import nitchcorp.backend.titan.immo.domain.enums.PropertyStatus;
import nitchcorp.backend.titan.immo.domain.enums.PropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    @Query("SELECT p FROM Property p LEFT JOIN FETCH p.owner LEFT JOIN FETCH p.agent WHERE p.trackingId = :trackingId")
    Optional<Property> getPropertiesByTrackingId(UUID trackingId);

    @Query("SELECT p FROM Property p  order by p.id DESC")
    List<Property> getAllProperties();

    @Query("SELECT p FROM Property p " +
            "WHERE (:city IS NULL OR p.city = :city) " +
            "AND (:country IS NULL OR p.country = :country) " +
            "AND (:priceMin IS NULL OR p.rentPrice >= :priceMin) " +
            "AND (:priceMax IS NULL OR p.rentPrice <= :priceMax) " +
            "AND (:type IS NULL OR p.type = :type) " +
            "AND (:availabilityDate IS NULL OR p.availabilityDate = :availabilityDate)")
    List<Property> searchProperties(@Param("city") String city,
                                    @Param("country") String country,
                                    @Param("priceMin") Double priceMin,
                                    @Param("priceMax") Double priceMax,
                                                                        @Param("type") PropertyType type,
                                    @Param("availabilityDate") LocalDate availabilityDate);

    Page<Property> findByAddress(@Param("name") String name, Pageable pageable);
    Page<Property> findByCountry(@Param("name") String name, Pageable pageable);
    Page<Property> findByCity(@Param("name") String name, Pageable pageable);


    List<Property> findByCountry(String country);

        @Query("SELECT p FROM Property p WHERE p.agent.trackingId = :agentId")
        List<Property> findByAgentTrackingId(@Param("agentId") UUID agentId);

        @Query("SELECT p FROM Property p WHERE p.owner.trackingId = :ownerId")
        List<Property> findByOwnerTrackingId(@Param("ownerId") UUID ownerId);

    List<Property> findByStatus(PropertyStatus status);

    List<Property> findByCountryAndStatus(String country, PropertyStatus status);
}
