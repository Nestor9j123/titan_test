package nitchcorp.backend.titan.coursier.Application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import nitchcorp.backend.titan.coursier.Application.dtos.requests.DeliveryOrderRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.DeliveryOrderResponse;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.PriceCalculationResponse;
import nitchcorp.backend.titan.coursier.Domain.enums.DeliveryStatus;
import nitchcorp.backend.titan.coursier.Application.services.DeliveryOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-orders")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Delivery Orders", description = "API de gestion des commandes de livraison")
public class DeliveryOrderController {

    private final DeliveryOrderService orderService;

    @Operation(
            summary = "Calculer le prix d’une livraison",
            description = "Calcule le coût d’une livraison selon la distance, le poids, etc."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Prix calculé avec succès",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PriceCalculationResponse.class))
    )
    @PostMapping("/calculate-price")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(
            @RequestBody DeliveryOrderRequest request) {
        return ResponseEntity.ok(orderService.calculatePrice(request));
    }

    @Operation(summary = "Créer une nouvelle commande")
    @ApiResponse(responseCode = "201", description = "Commande créée avec succès")
    @PostMapping
    public ResponseEntity<DeliveryOrderResponse> createOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Détails de la commande à créer",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DeliveryOrderRequest.class),
                            examples = @ExampleObject(
                                    value = "{ \"pickupAddress\": \"Lomé, Togo\", \"deliveryAddress\": \"Kara, Togo\", \"packageWeight\": 2.5, \"price\": 5000 }"
                            )
                    )
            )
            @RequestBody DeliveryOrderRequest request) {
        return new ResponseEntity<>(orderService.createOrder(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Récupérer toutes les commandes")
    @GetMapping
    public ResponseEntity<List<DeliveryOrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "Récupérer une commande par son trackingId")
    @GetMapping("/{trackingId}")
    public ResponseEntity<DeliveryOrderResponse> findByTrackingId(
            @Parameter(description = "UUID de la commande") @PathVariable UUID trackingId) {
        return ResponseEntity.ok(orderService.findByTrackingId(trackingId));
    }

    @Operation(summary = "Lister les commandes par statut")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DeliveryOrderResponse>> getOrderByStatus(
            @Parameter(description = "Statut de la livraison (PENDING, IN_PROGRESS, COMPLETED, CANCELED)")
            @PathVariable DeliveryStatus status) {
        return ResponseEntity.ok(orderService.getOrderByStatus(status));
    }

    @Operation(summary = "Lister les commandes d’une compagnie")
    @GetMapping("/company/{companyTrackingId}")
    public ResponseEntity<List<DeliveryOrderResponse>> getOrderByCompany(
            @Parameter(description = "UUID de la compagnie de livraison") @PathVariable UUID companyTrackingId) {
        return ResponseEntity.ok(orderService.getOrderByCompany(companyTrackingId));
    }

    @Operation(summary = "Lister les commandes assignées à un livreur")
    @GetMapping("/delivery-person/{deliveryPersonTrackingId}")
    public ResponseEntity<List<DeliveryOrderResponse>> getOrderByDeliveryPerson(
            @Parameter(description = "UUID du livreur") @PathVariable UUID deliveryPersonTrackingId) {
        return ResponseEntity.ok(orderService.getOrderByDeliveryPerson(deliveryPersonTrackingId));
    }

    @Operation(summary = "Assigner une commande à un livreur")
    @PatchMapping("/{orderTrackingId}/assign/{deliveryPersonTrackingId}")
    public ResponseEntity<DeliveryOrderResponse> assignToDeliveryPerson(
            @Parameter(description = "UUID de la commande") @PathVariable UUID orderTrackingId,
            @Parameter(description = "UUID du livreur") @PathVariable UUID deliveryPersonTrackingId) {
        return ResponseEntity.ok(orderService.assignToDeliveryPerson(orderTrackingId, deliveryPersonTrackingId));
    }

    @Operation(summary = "Démarrer une livraison")
    @PatchMapping("/{orderTrackingId}/start")
    public ResponseEntity<DeliveryOrderResponse> startDelivery(
            @Parameter(description = "UUID de la commande") @PathVariable UUID orderTrackingId) {
        return ResponseEntity.ok(orderService.startDelivery(orderTrackingId));
    }

    @Operation(summary = "Compléter une livraison")
    @PatchMapping("/{orderTrackingId}/complete")
    public ResponseEntity<DeliveryOrderResponse> completeDelivery(
            @Parameter(description = "UUID de la commande") @PathVariable UUID orderTrackingId) {
        return ResponseEntity.ok(orderService.completeDelivery(orderTrackingId));
    }

    @Operation(summary = "Annuler une commande")
    @PatchMapping("/{orderTrackingId}/cancel")
    public ResponseEntity<DeliveryOrderResponse> cancelOrder(
            @Parameter(description = "UUID de la commande")
            @PathVariable UUID orderTrackingId,

            @Parameter(description = "Raison de l’annulation (optionnelle)")
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(orderService.cancelOrder(orderTrackingId, reason));
    }


    @Operation(summary = "Supprimer une commande")
    @DeleteMapping("/{orderTrackingId}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "UUID de la commande") @PathVariable UUID orderTrackingId) {
        orderService.deleteOrder(orderTrackingId);
        return ResponseEntity.noContent().build();
    }
}
