package nitchcorp.backend.titan.coursier.Application.dtos.responses;

import java.math.BigDecimal;
import java.util.UUID;

public record PricingRuleResponse(
         UUID trackingId,

         Double minDistanceKm,

         Double maxDistanceKm,

         BigDecimal basePrice,

         BigDecimal pricePerKm,

         UUID deliveryCompanyTrackingId
) {
}
