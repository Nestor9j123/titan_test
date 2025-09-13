package nitchcorp.backend.titan.food.Domain.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="OPTIONS_PERSONALISERS")
@Data
public class OptionPersonaliser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID trackingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plat_id")
    private Plat plat;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double prix;

}
