package nitchcorp.backend.titan.food.Domain.models;

import jakarta.persistence.*;
import lombok.Data;
import nitchcorp.backend.titan.food.Domain.enums.StatusCommande;
import nitchcorp.backend.titan.shared.securite.user.entities.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "COMMANDES")
@Data
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false , length = 20)
    private LocalDateTime dateCommande;

    @Column(nullable = false , length = 20)
    private double prixTotal;

    @Column(nullable = false , length = 20)
    private String addressLivaraison;

    @Column(nullable = false , length = 10)
    @Enumerated(EnumType.STRING)
    private StatusCommande status;

    @Column(nullable = false , length = 20,unique = true)
    private UUID trackingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

}
