package nitchcorp.backend.titan.coursier.Application.dtos.responses;

import java.math.BigDecimal;

public record PriceCalculationResponse(
        Double distanceInKm,
        BigDecimal calculatedPrice,
        String message
) {
}
