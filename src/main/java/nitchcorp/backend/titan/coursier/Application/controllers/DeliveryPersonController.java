package nitchcorp.backend.titan.coursier.Application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import nitchcorp.backend.titan.coursier.Application.dtos.requests.DeliveryPersonRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.DeliveryPersonResponse;
import nitchcorp.backend.titan.coursier.Application.services.DeliveryPersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-persons")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Delivery Person", description = "Gestion des livreurs")
public class DeliveryPersonController {

    private final DeliveryPersonService personService;

    @Operation(summary = "Créer un livreur")
    @PostMapping
    public ResponseEntity<DeliveryPersonResponse> createDeliveryPerson(
            @RequestBody DeliveryPersonRequest request) {
        return new ResponseEntity<>(personService.createDeliveryPerson(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Lister tous les livreurs")
    @GetMapping
    public ResponseEntity<List<DeliveryPersonResponse>> getAllDeliveryPersons() {
        return ResponseEntity.ok(personService.getAllDeliveryPersons());
    }

    @Operation(summary = "Récupérer un livreur par son trackingId")
    @GetMapping("/{trackingId}")
    public ResponseEntity<DeliveryPersonResponse> getDeliveryPersonByTrackingId(
            @Parameter(description = "UUID du livreur") @PathVariable UUID trackingId) {
        return ResponseEntity.ok(personService.getDeliveryPersonByTrackingId(trackingId));
    }

    @Operation(summary = "Lister les livreurs d’une société")
    @GetMapping("/company/{companyTrackingId}")
    public ResponseEntity<List<DeliveryPersonResponse>> getDeliveryPersonByCompany(
            @Parameter(description = "UUID de la société") @PathVariable UUID companyTrackingId) {
        return ResponseEntity.ok(personService.getDeliveryPersonByCompany(companyTrackingId));
    }

    @Operation(summary = "Lister les livreurs disponibles d’une société")
    @GetMapping("/company/{companyTrackingId}/available")
    public ResponseEntity<List<DeliveryPersonResponse>> getDeliveryPersonAvailableByCompany(
            @Parameter(description = "UUID de la société") @PathVariable UUID companyTrackingId) {
        return ResponseEntity.ok(personService.getDeliveryPersonAvailableByCompany(companyTrackingId));
    }

    @Operation(summary = "Mettre à jour un livreur")
    @PutMapping("/{trackingId}")
    public ResponseEntity<DeliveryPersonResponse> updateDeliveryPerson(
            @Parameter(description = "UUID du livreur") @PathVariable UUID trackingId,
            @RequestBody DeliveryPersonRequest request) {
        return ResponseEntity.ok(personService.updateDeliveryPerson(trackingId, request));
    }

    @Operation(summary = "Changer la disponibilité d’un livreur")
    @PatchMapping("/{trackingId}/availability")
    public ResponseEntity<Void> setAvailability(
            @Parameter(description = "UUID du livreur") @PathVariable UUID trackingId,
            @RequestParam boolean isAvailable) {
        personService.setAvailability(trackingId, isAvailable);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Désactiver un livreur")
    @PatchMapping("/{trackingId}/deactivate")
    public ResponseEntity<Void> deactivateDeliveryPerson(
            @Parameter(description = "UUID du livreur") @PathVariable UUID trackingId) {
        personService.deactivateDeliveryPerson(trackingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Supprimer un livreur")
    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> deleteDeliveryPerson(
            @Parameter(description = "UUID du livreur") @PathVariable UUID trackingId) {
        personService.deleteDeliveryPerson(trackingId);
        return ResponseEntity.noContent().build();
    }
}
