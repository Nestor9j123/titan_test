package nitchcorp.backend.titan.food.Application.mappers;

import nitchcorp.backend.titan.food.Application.dtos.request.PlatCommandeRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.OptionPersonaliserResponse;
import nitchcorp.backend.titan.food.Application.dtos.response.PlatCommandeResponse;
import nitchcorp.backend.titan.food.Domain.models.Commande;
import nitchcorp.backend.titan.food.Domain.models.Plat;
import nitchcorp.backend.titan.food.Domain.models.PlatCommande;
import nitchcorp.backend.titan.food.Infrastructure.CommandeRepository;
import nitchcorp.backend.titan.food.Infrastructure.PlatCommandeRepository;
import nitchcorp.backend.titan.food.Infrastructure.PlatRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PlatCommandeMapper {

    private final PlatRepository platRepository;
    private final CommandeRepository commandeRepository;
    private final PlatCommandeRepository platCommandeRepository;

    public PlatCommandeMapper(PlatRepository platRepository,
                              CommandeRepository commandeRepository ,
                              PlatCommandeRepository platCommandeRepository) {

        this.platRepository = platRepository;
        this.commandeRepository = commandeRepository;
        this.platCommandeRepository = platCommandeRepository;
    }

    public PlatCommande toEntity(PlatCommandeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La demande PlatCommande est null");
        }

        PlatCommande platCommande = new PlatCommande();
        platCommande.setTrackingId(UUID.randomUUID());
        platCommande.setQuantite(request.quantite());
        platCommande.setPrix(request.prix());
        platCommande.setTempsPreparation(request.tempsPreparation());

        Plat plat = platRepository.findByTrackingId(request.tracking_platId())
                .orElseThrow(() -> new IllegalArgumentException("Plat introuvable avec le numéro : " + request.tracking_platId()));
        platCommande.setPlat(plat);

        Commande commande = commandeRepository.findByTrackingId(request.tracking_commandeId())
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable avec le numéro : " + request.tracking_commandeId()));
        platCommande.setCommande(commande);
        return platCommande;
    }


    public PlatCommandeResponse toResponse(PlatCommande platCommande, List<OptionPersonaliserResponse> options) {
        if(platCommande == null) {
            throw new IllegalArgumentException(" le plat commandé est null");
        }

        return new PlatCommandeResponse(
                platCommande.getPlat().getTrackingId(),
                platCommande.getCommande().getTrackingId(),
                platCommande.getQuantite(),
                platCommande.getPrix(),
                platCommande.getTempsPreparation(),
                platCommande.getTrackingId(),
                options

        );
    }

    public PlatCommandeResponse toResponse(PlatCommande platCommande){
        return toResponse(platCommande, List.of());
    }

    public static PlatCommande toEntityFromResponse(PlatCommandeResponse response) {
        if (response == null) {
            throw new IllegalArgumentException(" réponse PlatCommande est null");
        }

        PlatCommande platCommande = new PlatCommande();
        platCommande.setTrackingId(response.trackingId());
        platCommande.setQuantite(response.quantite());
        platCommande.setPrix(response.prix());
        platCommande.setTempsPreparation(response.tempsPreparation());

        return platCommande;
    }
}
