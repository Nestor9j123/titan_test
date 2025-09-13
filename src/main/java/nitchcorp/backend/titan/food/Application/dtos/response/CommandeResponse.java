package nitchcorp.backend.titan.food.Application.dtos.response;

import lombok.Builder;
import nitchcorp.backend.titan.food.Domain.enums.StatusCommande;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommandeResponse (

         UUID trackingId,

         LocalDateTime dateCommande,

         double prixTotal,

         String addressLivaraison,

         StatusCommande status,

         UUID tracking_userId
){ }
