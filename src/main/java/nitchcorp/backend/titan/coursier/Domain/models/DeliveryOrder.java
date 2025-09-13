package nitchcorp.backend.titan.coursier.Domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nitchcorp.backend.titan.coursier.Domain.enums.DeliveryStatus;
import nitchcorp.backend.titan.shared.utils.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DELIVERY_ORDERS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryOrder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, updatable = false)
    private UUID trackingId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String details;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetAddress", column = @Column(name = "pickup_street_address")),
            @AttributeOverride(name = "city", column = @Column(name = "pickup_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "pickup_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "pickup_country")),
            @AttributeOverride(name = "additionalInfo", column = @Column(name = "pickup_additional_info")),
            @AttributeOverride(name = "latitude", column = @Column(name = "pickup_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "pickup_longitude"))
    })
    private Address pickupAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "contactName", column = @Column(name = "pickup_contact_name")),
            @AttributeOverride(name = "contactPhone", column = @Column(name = "pickup_contact_phone"))
    })
    private ContactInfo pickupContact;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetAddress", column = @Column(name = "delivery_street_address")),
            @AttributeOverride(name = "city", column = @Column(name = "delivery_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "delivery_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "delivery_country")),
            @AttributeOverride(name = "additionalInfo", column = @Column(name = "delivery_additional_info")),
            @AttributeOverride(name = "latitude", column = @Column(name = "delivery_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "delivery_longitude"))
    })
    private Address deliveryAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "contactName", column = @Column(name = "delivery_contact_name")),
            @AttributeOverride(name = "contactPhone", column = @Column(name = "delivery_contact_phone"))
    })
    private ContactInfo deliveryContact;

    @Column(nullable = false)
    private Double distanceInKm;

    @Column(nullable = false)
    private BigDecimal calculatedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_company_id", nullable = false)
    private DeliveryCompany deliveryCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_delivery_person_id")
    private DeliveryPerson assignedDeliveryPerson;


}

