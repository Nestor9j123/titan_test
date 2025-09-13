package nitchcorp.backend.titan.food.Application.mappers;

import nitchcorp.backend.titan.food.Application.dtos.request.CommandeRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.CommandeResponse;
import nitchcorp.backend.titan.food.Domain.models.Commande;
import nitchcorp.backend.titan.food.Domain.models.PlatCommande;

import nitchcorp.backend.titan.food.Domain.enums.StatusCommande;
import nitchcorp.backend.titan.food.Infrastructure.PlatCommandeRepository;

import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.securite.user.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CommandeMapper {

    private final UserRepository userRepository;
    private final PlatCommandeRepository platCommandeRepository;

    public CommandeMapper(UserRepository userRepository, PlatCommandeRepository platCommandeRepository) {
        this.userRepository = userRepository;
        this.platCommandeRepository = platCommandeRepository;
    }

    public Commande toEntity(CommandeRequest request) {

        Double price = 0.0;
        if (request == null) {
            throw new IllegalArgumentException("La requête CommandeRequest ne peut pas être nulle");
        }

        Commande commande = new Commande();
        commande.setTrackingId(UUID.randomUUID());
        commande.setDateCommande(request.dateCommande());
        commande.setStatus(StatusCommande.RECUE);
        commande.setAddressLivaraison(request.addressLivaraison());
        commande.setPrixTotal(request.prixTotal());

        List<PlatCommande> platCommandes = platCommandeRepository.findAllByCommande(commande.getTrackingId());
        for(PlatCommande platCommande : platCommandes){
            price = price + platCommande.getPrix();
        }

        commande.setPrixTotal(price);
        System.out.println(request.tracking_userId());
        User user = userRepository.findByTrackingId(request.tracking_userId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + request.tracking_userId()));
        commande.setUser(user);
        return commande;
    }


    public CommandeResponse toResponse(Commande commande) {

        if (commande == null) {
            throw new IllegalArgumentException("L'entité Commande ne peut pas être nulle");
        }

        return new CommandeResponse(
                commande.getTrackingId(),
                commande.getDateCommande(),
                commande.getPrixTotal(),
                commande.getAddressLivaraison(),
                commande.getStatus(),
                commande.getUser().getTrackingId()
        );
    }

    public static Commande toEntityFromResponse(CommandeResponse response){
        if(response == null){
            throw  new IllegalArgumentException("la commande est null");
        }

        Commande commande = new Commande();
        commande.setTrackingId(response.trackingId());
        commande.setDateCommande(response.dateCommande());
        commande.setPrixTotal(response.prixTotal());
        commande.setStatus(StatusCommande.RECUE);
        commande.setAddressLivaraison(response.addressLivaraison());
        return commande;
    }

}
