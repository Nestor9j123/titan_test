package nitchcorp.backend.titan.food.Application.dtos.request;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record PlatCommandeRequest(

        UUID  tracking_platId,

        UUID tracking_commandeId,

        int quantite,

        double prix,

        String tempsPreparation,

        List<Long> OptionIds

) {

}
