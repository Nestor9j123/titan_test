package nitchcorp.backend.titan.immo.domain.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nitchcorp.backend.titan.immo.domain.enums.PropertyStatus;
import nitchcorp.backend.titan.immo.domain.enums.PropertyType;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.utils.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "PROPERTY")
@Getter
@Setter
public class Property extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;

    @Enumerated(EnumType.STRING)
    private PropertyType type;

    private String address;
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    @Column(length = 500)
    private String description;
    private BigDecimal rentPrice;
    private BigDecimal additionalFees;
    private BigDecimal deposit;
    private Integer numberOfRooms;
    private Double area;
    @ElementCollection
    private List<String> amenities;
    @Enumerated(EnumType.STRING)
    private PropertyStatus status;

    @ElementCollection
    private List<String> photos;

    private UUID trackingId;
    private LocalDate availabilityDate;
}
