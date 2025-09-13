package nitchcorp.backend.titan.food.Application.dtos.request;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record PlatRequest(

        String name,

        String description,

        String categorie,

        double prix,

        String tempsDePreparation,

        UUID tracking_restaurantId,

        double rate ,

        List<String> imagesurl

) {
    public PlatRequest {
        if (rate > 1) {
            throw new IllegalArgumentException("Le taux ne doit pas dépasser 1.");
        }
    }
}
