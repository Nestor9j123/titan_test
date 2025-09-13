package nitchcorp.backend.titan.coursier.Domain.events.consumer;

import jakarta.transaction.Transactional;
import nitchcorp.backend.titan.food.Domain.events.CommandeRequestEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsumeFoodService {

    private RestaurantPositionResponse lastKnownPosition;


    @Transactional
    @EventListener
    public void handleCommandeRequestEvent(CommandeRequestEvent event) {
        this.lastKnownPosition = new RestaurantPositionResponse(
                event.commandeTrackingId(),
                event.addressLivaraison(),
                event.latitude(),
                event.longitude()
        );
    }


    public RestaurantPositionResponse getRestaurantPosition() {
        if (lastKnownPosition == null) {
            return new RestaurantPositionResponse(
                    UUID.randomUUID(),
                    "Aucune position connue",
                    0.0,
                    0.0
            );
        }
        return lastKnownPosition;
    }

}
