package nitchcorp.backend.titan.coursier.Application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import nitchcorp.backend.titan.coursier.Domain.events.consumer.ConsumeFoodService;
import nitchcorp.backend.titan.coursier.Domain.events.consumer.RestaurantPositionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consumer/restaurants")
@CrossOrigin(origins = "*")
@Tag(name = "Consumer - Restaurants", description = "Consommation des événements liés aux restaurants")
public class ConsumeFoodController {

    private final ConsumeFoodService consumerService;

    public ConsumeFoodController(ConsumeFoodService consumerService) {
        this.consumerService = consumerService;
    }

    @Operation(summary = "Obtenir la position d’un restaurant",
            description = "Récupère la position actuelle envoyée par le service restaurant via un événement.")
    @GetMapping("/position")
    public ResponseEntity<RestaurantPositionResponse> getRestaurantPosition() {
        RestaurantPositionResponse response = consumerService.getRestaurantPosition();
        return ResponseEntity.ok(response);
    }
}
