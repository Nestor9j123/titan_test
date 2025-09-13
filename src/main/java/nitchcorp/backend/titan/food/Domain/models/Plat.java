package nitchcorp.backend.titan.food.Domain.models;

import jakarta.persistence.*;
import lombok.Data;
import nitchcorp.backend.titan.food.Domain.enums.CategoriePlat;
import java.util.*;

@Entity
@Table(name = "PLATS")
@Data
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID trackingId;

    @Column(nullable = false , length = 35)
    private String name;

    @Column(nullable = false , length = 50)
    private String description;

    @Enumerated(EnumType.STRING)
    private CategoriePlat categorie;

    @Column(nullable = false)
    private double prix;

    @Column(nullable = false , length = 50)
    private String tempsDePreparation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;


    private int note = 0 ;

    private double rate = 1.0;

    private double prixFinal = 0.0;

    @ElementCollection
    private List<String>  imagesurl;


}

