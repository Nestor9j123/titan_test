package nitchcorp.backend.titan.food.Domain.events;

import org.springframework.modulith.NamedInterface;

import java.util.UUID;

@NamedInterface
public record CommandeRequestEvent(
        String restaurantName,
        UUID commandeTrackingId,
        String addressLivaraison,
        double latitude,
        double longitude
) {
}
