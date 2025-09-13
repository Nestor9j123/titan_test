package nitchcorp.backend.titan.food.Application.dtos.request;

import lombok.Builder;
import nitchcorp.backend.titan.food.Domain.enums.StatusCommande;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommandeRequest (

        LocalDateTime dateCommande,

        double prixTotal,

        String addressLivaraison,

        StatusCommande status,

        UUID tracking_userId
){ }
