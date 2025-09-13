package nitchcorp.backend.titan.food.Application.services;

import nitchcorp.backend.titan.food.Application.dtos.request.CommandeRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.CommandeResponse;

import java.util.List;
import java.util.UUID;

public interface CommandeService {

    CommandeResponse createCommande(CommandeRequest request);

    CommandeResponse getCommandeByTrackingId(UUID trackingId);

    List<CommandeResponse> getAllCommandes();

    CommandeResponse updateCommande(UUID trackingId, CommandeRequest request);

    void deleteCommande(UUID trackingId);

}
