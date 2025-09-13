package nitchcorp.backend.titan.coursier.Domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nitchcorp.backend.titan.shared.utils.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "DELVERY_COMPANIES")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryCompany extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tracking_id", nullable = false)
    private UUID trackingId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String contactEmail;

    private String contactPhone;

    private String address;

    private Boolean isActive = true;

}
