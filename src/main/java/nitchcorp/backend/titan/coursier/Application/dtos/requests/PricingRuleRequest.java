package nitchcorp.backend.titan.coursier.Application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PricingRuleRequest(
        @NotNull(message = "La distance minimale est obligatoire")
         Double minDistanceKm,

         @NotNull(message = "La distance maximale est obligatoire")
         Double maxDistanceKm,

         @NotNull(message = "Le prix de base est obligatoire")
         BigDecimal basePrice,

         @NotNull(message = "Le prix par km est obligatoire")
         BigDecimal pricePerKm,

         @NotNull(message = "L'ID de la compagnie de livraison est obligatoire")
         UUID deliveryCompanyTrackingId
) {
    @JsonCreator
    public PricingRuleRequest(
            @JsonProperty("minDistanceKm") Double minDistanceKm,
            @JsonProperty("maxDistanceKm") Double maxDistanceKm,
            @JsonProperty("basePrice") BigDecimal basePrice,
            @JsonProperty("pricePerKm") BigDecimal pricePerKm,
            @JsonProperty("deliveryCompanyId") UUID deliveryCompanyTrackingId) {
        this.minDistanceKm = minDistanceKm;
        this.maxDistanceKm = maxDistanceKm;
        this.basePrice = basePrice;
        this.pricePerKm = pricePerKm;
        this.deliveryCompanyTrackingId = deliveryCompanyTrackingId;
    }
}
