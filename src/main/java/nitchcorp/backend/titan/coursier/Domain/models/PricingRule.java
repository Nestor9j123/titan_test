package nitchcorp.backend.titan.coursier.Domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nitchcorp.backend.titan.shared.utils.BaseEntity;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "PRICING_RULES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PricingRule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID trackingId;

    @Column(nullable = false)
    private Double minDistanceKm;

    @Column(nullable = false)
    private Double maxDistanceKm;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private BigDecimal pricePerKm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_company_id", nullable = false)
    private DeliveryCompany deliveryCompany;


}
