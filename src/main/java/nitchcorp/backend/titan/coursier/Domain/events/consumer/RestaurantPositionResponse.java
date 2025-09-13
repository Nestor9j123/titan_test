package nitchcorp.backend.titan.coursier.Domain.events.consumer;

import java.util.UUID;

public record RestaurantPositionResponse(
        UUID commandeTrackingId,
        String addresseLivraison,
        Double latitude,
        Double longitude
) {
}
