package nitchcorp.backend.titan.coursier.Domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nitchcorp.backend.titan.coursier.Domain.enums.TypeVehicle;
import nitchcorp.backend.titan.shared.securite.user.entities.User;

@Entity
@Table(name = "DELIVERY_PERSONS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPerson extends User {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeVehicle vehicleType;

    @Column(nullable = true)
    private String licenseNumber;

    private Boolean isAvailable = true;

    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_company_id", nullable = false)
    private DeliveryCompany deliveryCompany;
}
