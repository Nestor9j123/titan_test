package nitchcorp.backend.titan.immo.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.immo.application.dto.requests.NotificationRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.NotificationResponse;
import nitchcorp.backend.titan.immo.domain.exceptions.NotificationNotFoundException;
import nitchcorp.backend.titan.immo.application.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "NotificationController", description = "API pour NotificationController")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/create")
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request) {
        log.info("Received request: recipientId={}, recipientType={}, message={}",
                request.recipientId(), request.recipientType(), request.message());

        if (request.recipientId() == null) {
            log.error("RecipientId is null in the request. Full request: {}", request);
            return ResponseEntity.badRequest().body(null);
        }

        log.info("Creating notification for recipient: {}", request.recipientId());

        try {
            NotificationResponse response = notificationService.createNotification(request);
            log.info("Notification created successfully with ID: {}", response.trackingId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NotificationNotFoundException e) {
            log.error("Notification creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for creating notification: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error creating notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        log.info("Fetching all notifications");
        try {
            List<NotificationResponse> notifications = notificationService.getAllNotifications();
            log.info("Retrieved {} notifications", notifications.size());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error fetching all notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<NotificationResponse> getNotificationByTrackingId(@PathVariable UUID trackingId) {
        log.info("Fetching notification with trackingId: {}", trackingId);
        try {
            NotificationResponse notification = notificationService.getNotificationByTrackingId(trackingId);
            log.info("Notification found with trackingId: {}", trackingId);
            return ResponseEntity.ok(notification);
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Notification not found with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{trackingId}")
    public ResponseEntity<NotificationResponse> updateNotification(
            @PathVariable UUID trackingId, @Valid @RequestBody NotificationRequest request) {
        log.info("Updating notification with trackingId: {}", trackingId);
        try {
            NotificationResponse updatedNotification = notificationService.updateNotification(trackingId, request);
            log.info("Notification updated successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok(updatedNotification);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for updating notification: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating notification with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{trackingId}")
    public ResponseEntity<String> deleteNotification(@PathVariable UUID trackingId) {
        log.info("Deleting notification with trackingId: {}", trackingId);
        try {
            notificationService.deleteNotification(trackingId);
            log.info("Notification deleted successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok("Notification supprimée avec succès");
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId for deletion: {}", e.getMessage());
            return ResponseEntity.badRequest().body("ID de tracking invalide");
        } catch (Exception e) {
            log.error("Error deleting notification with trackingId: {}", trackingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification non trouvée");
        }
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<String> deleteNotificationRestful(@PathVariable UUID trackingId) {
        return deleteNotification(trackingId);
    }

    // Endpoints supplémentaires pour les notifications selon le cahier des charges

    @GetMapping("/unread/recipient/{recipientId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByRecipient(@PathVariable UUID recipientId) {
        log.info("Fetching notifications for recipient: {}", recipientId);
        try {
            List<NotificationResponse> notifications = notificationService.getNotificationsByRecipient(recipientId);
            log.info("Retrieved {} notifications for recipient: {}", notifications.size(), recipientId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error fetching notifications for recipient: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByType(@PathVariable String type) {
        log.info("Fetching notifications of type: {}", type);
        try {
            List<NotificationResponse> notifications = notificationService.getNotificationsByType(type);
            log.info("Retrieved {} notifications of type: {}", notifications.size(), type);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error fetching notifications by type: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/recipient/{recipientId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(@PathVariable UUID recipientId) {
        log.info("Fetching unread notifications for recipient: {}", recipientId);
        try {
            List<NotificationResponse> notifications = notificationService.getUnreadNotifications(recipientId);
            log.info("Retrieved {} unread notifications for recipient: {}", notifications.size(), recipientId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error fetching unread notifications: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{trackingId}/mark-read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID trackingId) {
        log.info("Marking notification as read: {}", trackingId);
        try {
            NotificationResponse notification = notificationService.markAsRead(trackingId);
            log.info("Notification marked as read successfully");
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            log.error("Error marking notification as read: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/recipient/{recipientId}/read-all")
    public ResponseEntity<String> markAllAsRead(@PathVariable UUID recipientId) {
        log.info("Marking all notifications as read for recipient: {}", recipientId);
        try {
            int count = notificationService.markAllAsRead(recipientId);
            log.info("{} notifications marked as read", count);
            return ResponseEntity.ok(count + " notifications marquées comme lues");
        } catch (Exception e) {
            log.error("Error marking all notifications as read: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur lors du marquage des notifications");
        }
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByPriority(@PathVariable String priority) {
        log.info("Fetching notifications with priority: {}", priority);
        try {
            List<NotificationResponse> notifications = notificationService.getNotificationsByPriority(priority);
            log.info("Retrieved {} notifications with priority: {}", notifications.size(), priority);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error fetching notifications by priority: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/push")
    public ResponseEntity<String> sendPushNotification(
            @RequestParam List<UUID> recipientIds,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam(required = false) String priority) {
        log.info("Sending push notification to {} recipients", recipientIds.size());
        try {
            int sent = notificationService.sendPushNotification(recipientIds, title, message, priority);
            log.info("Push notification sent to {} recipients", sent);
            return ResponseEntity.ok("Notification push envoyée à " + sent + " destinataires");
        } catch (Exception e) {
            log.error("Error sending push notification: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur lors de l'envoi de la notification push");
        }
    }

    @DeleteMapping("/recipient/{recipientId}/read")
    public ResponseEntity<String> deleteReadNotifications(@PathVariable UUID recipientId) {
        log.info("Deleting read notifications for recipient: {}", recipientId);
        try {
            int deleted = notificationService.deleteReadNotifications(recipientId);
            log.info("{} read notifications deleted", deleted);
            return ResponseEntity.ok(deleted + " notifications lues supprimées");
        } catch (Exception e) {
            log.error("Error deleting read notifications: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur lors de la suppression des notifications lues");
        }
    }
}
