package nitchcorp.backend.titan.food.Application.services;

import nitchcorp.backend.titan.food.Application.dtos.request.PlatCommandeRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.PlatCommandeResponse;
import nitchcorp.backend.titan.food.Domain.models.PlatCommande;

import java.util.List;
import java.util.UUID;

public interface PlatCommandeService {

    PlatCommandeResponse createPlatCommande(PlatCommandeRequest request);

    PlatCommandeResponse updatePlatCommande(UUID trackingId, PlatCommandeRequest request);

    void deletePlatCommande(UUID trackingId);

    PlatCommandeResponse getPlatCommande(UUID trackingId);

    List<PlatCommandeResponse> getAllPlatCommandes();

    List<PlatCommandeResponse> getAllPlatCommmandeByCommande(UUID trackingId);

    PlatCommandeResponse addOptionToPlatCommande(UUID trackingId, UUID optionId);

    void deleteOptionToPlatCommande(UUID trackingId , UUID optionId);

    double getTotalPrice(PlatCommande platCommande) ;
}
