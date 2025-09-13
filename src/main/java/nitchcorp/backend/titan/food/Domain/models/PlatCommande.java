package nitchcorp.backend.titan.food.Domain.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Data
@Table(name = "PLAT_COMMANDES")
public class PlatCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "plat_id")
    private Plat plat;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "commande_id")
    private Commande commande;

    @Column(length = 5 , nullable = false)
    private int quantite;

    @Column(length = 5 , nullable = false)
    private double prix;

    @Column(length = 20 , nullable = false)
    private String tempsPreparation;

    @Column(length = 20 , nullable = false , unique = true)
    private UUID trackingId;

    @ElementCollection
    @CollectionTable(
            name = "plat_commande_options",
            joinColumns = @JoinColumn(name = "plat_commande_id")
    )
    @Column(name = "option_id")
    private List<Long> OptionIds = new ArrayList<>();


    public void addOptionId(Long optionId) {
        if(!this.OptionIds.contains(optionId)) {
            this.OptionIds.add(optionId);
        }
    }

    public void removeOptionId(Long optionId) {
        this.OptionIds.remove(optionId);
    }
    public boolean hasOptionId() {
        return  !this.OptionIds.isEmpty();
    }

    public int getOptionIdSize() {
        return this.OptionIds.size();
    }


}
