package nitchcorp.backend.titan.events.Application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.events.Application.dtos.request.EventsRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.EventsResponse;
import nitchcorp.backend.titan.events.Application.service.EventsService;
import nitchcorp.backend.titan.shared.minio.service.MinioService;
import nitchcorp.backend.titan.shared.utils.constantSecurities.SecurityConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Events", description = "API pour la gestion des événements")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventsController {

    private final EventsService eventsService;
    private final MinioService minioService;

    @GetMapping("/all")
    @Operation(summary = "Récupérer tous les événements", description = "Récupère la liste de tous les événements")
    @ApiResponse(responseCode = "200", description = "Liste des événements récupérée avec succès")
    public ResponseEntity<List<EventsResponse>> getAllEvents() {
        log.info("Fetching all events at {}", LocalDateTime.now());
        try {
            List<EventsResponse> events = eventsService.getAllEvents();
            log.info("Retrieved {} events", events.size());
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("Error fetching all events: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer un événement par ID de suivi", description = "Récupère un événement spécifique par son ID de suivi")
    @ApiResponse(responseCode = "200", description = "Événement trouvé")
    @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    public ResponseEntity<EventsResponse> getEventByTrackingId(
            @Parameter(description = "ID de suivi de l'événement", required = true)
            @PathVariable UUID trackingId) {
        log.info("Fetching event with trackingId: {}", trackingId);
        try {
            EventsResponse event = eventsService.getEventByTrackingId(trackingId);
            log.info("Event found with trackingId: {}", trackingId);
            return ResponseEntity.ok(event);
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Event not found with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }



    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Créer un événement", description = "Crée un nouvel événement avec éventuellement des images")
    public ResponseEntity<EventsResponse> createEvent(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("capacity") int capacity,
            @RequestParam("startDateTime") String startDateTimeStr,
            @RequestParam("endDateTime") String endDateTimeStr,
            @RequestParam("organizerId") UUID organizerId,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        log.info("Creating event: {}", name);

        try {
            // Convertir les chaînes en LocalDateTime
            LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeStr);
            LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeStr);

            // Construire le EventsRequest
            EventsRequest request = new EventsRequest(
                    name,
                    description,
                    capacity,
                    startDateTime,
                    endDateTime,
                    null, // Les images seront gérées via MultipartFile
                    organizerId
            );

            EventsResponse eventsResponse = eventsService.createEventWithPhotos(request, images);
            log.info("Event created successfully with trackingId: {}", eventsResponse.trackingId());
            return ResponseEntity.status(HttpStatus.CREATED).body(eventsResponse);

        } catch (Exception e) {
            log.error("Error creating event: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @PutMapping("/update/{trackingId}")
    @Operation(summary = "Mettre à jour un événement", description = "Met à jour les informations d'un événement existant")
    @ApiResponse(responseCode = "200", description = "Événement mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    public ResponseEntity<EventsResponse> updateEvent(
            @PathVariable UUID trackingId,
            @Valid @RequestBody EventsRequest request) {
        log.info("Updating event with trackingId: {}", trackingId);
        try {
            EventsResponse updatedEvent = eventsService.updateEvent(trackingId, request);
            log.info("Event updated successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for updating event: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating event with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{trackingId}")
    @Operation(summary = "Supprimer un événement", description = "Supprime un événement du système")
    @ApiResponse(responseCode = "200", description = "Événement supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    public ResponseEntity<String> deleteEvent(@PathVariable UUID trackingId) {
        log.info("Deleting event with trackingId: {}", trackingId);
        try {
            eventsService.deleteEvent(trackingId);
            log.info("Event deleted successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok("Événement supprimé avec succès");
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId for deletion: {}", e.getMessage());
            return ResponseEntity.badRequest().body("ID de tracking invalide");
        } catch (Exception e) {
            log.error("Error deleting event with trackingId: {}", trackingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Événement non trouvé");
        }
    }

    @PostMapping(value = "/{trackingId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Ajouter des images à un événement", description = "Ajoute une ou plusieurs images à un événement existant")
    public ResponseEntity<EventsResponse> addImagesToEvent(
            @PathVariable UUID trackingId,
            @RequestParam("images") List<MultipartFile> images) {
        log.info("Adding {} images to event: {}", images.size(), trackingId);
        try {
            if (images == null || images.isEmpty()) {
                log.error("No images provided for event: {}", trackingId);
                return ResponseEntity.badRequest().build();
            }
            EventsResponse event = eventsService.addImagesToEvent(trackingId, images);
            log.info("Images added successfully to event: {}", trackingId);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            log.error("Error adding images to event: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{trackingId}/images")
    public ResponseEntity<EventsResponse> removeImagesFromEvent(
            @PathVariable UUID trackingId,
            @RequestBody List<Long> imageIds) {
        log.info("Removing images {} from event {}", imageIds, trackingId);
        EventsResponse updatedEvent = eventsService.removeImagesFromEvent(trackingId, imageIds);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/{trackingId}/images")
    @Operation(summary = "Récupérer toutes les images d'un événement", description = "Retourne la liste des URLs de toutes les images d'un événement")
    @ApiResponse(responseCode = "200", description = "Images récupérées avec succès")
    @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    public ResponseEntity<List<String>> getAllEventImages(@PathVariable UUID trackingId) {
        log.info("Getting all images for event: {}", trackingId);
        try {
            EventsResponse event = eventsService.getEventByTrackingId(trackingId);
            if (event.images() == null || event.images().isEmpty()) {
                log.info("No images found for event: {}", trackingId);
                return ResponseEntity.ok(List.of());
            }
            log.info("Found {} images for event: {}", event.images().size(), trackingId);
            return ResponseEntity.ok(event.images());
        } catch (Exception e) {
            log.error("Error getting images for event {}: {}", trackingId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
