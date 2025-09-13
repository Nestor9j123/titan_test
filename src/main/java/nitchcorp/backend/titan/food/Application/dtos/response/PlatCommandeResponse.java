package nitchcorp.backend.titan.food.Application.dtos.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record PlatCommandeResponse(

        UUID tracking_platId,

        UUID tracking_commandeId,

        int quantite,

        double prix,

        String tempsPreparation,

        UUID trackingId,

        List<OptionPersonaliserResponse> OptionIds
) {
}
