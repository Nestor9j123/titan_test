package nitchcorp.backend.titan.food.Application.dtos.response;

import lombok.Builder;
import nitchcorp.backend.titan.food.Domain.enums.CategoriePlat;

import java.util.List;
import java.util.UUID;

@Builder
public record PlatResponse(

        UUID trackingId,

        UUID tracking_restaurantId,

        String name,

        String description,

        CategoriePlat categorie,

        double prix,

        String tempsDePreparation,

        double rate ,

        double prixFinal ,

        List<String> imagesurl


) { }
