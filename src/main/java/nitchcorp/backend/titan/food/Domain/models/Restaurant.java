package nitchcorp.backend.titan.food.Domain.models;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "RESTAURANTS")
@Data
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true )
    private UUID trackingId;

    @Column(length = 20 , nullable = false)
    private String name;

    @Column(length = 20 , nullable = false)
    private String phone;

    @Column(length = 20 , nullable = false)
    private String address;

    @Column(length = 200 , nullable = false)
    private String description;

    @Column(length = 20 , nullable = false)
    private String kitchenType;

    @Column(length = 20 , nullable = true)
    private Double latitude;

    @Column(length = 20 , nullable = true)
    private Double longitude;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime openingHour;

    @Column(nullable = false)
    private boolean haveDelevery= false;

    @Column(length = 100 , nullable = true)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> imageUrl;
}
