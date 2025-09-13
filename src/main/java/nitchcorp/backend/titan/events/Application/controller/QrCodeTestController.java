package nitchcorp.backend.titan.events.Application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.events.Application.service.QrCodeService;
import nitchcorp.backend.titan.events.Application.service.ImplService.QrCodeServiceImpl;
import nitchcorp.backend.titan.events.domain.model.Events;
import nitchcorp.backend.titan.events.domain.model.PurchasedTicket;
import nitchcorp.backend.titan.events.domain.model.TicketTemplate;
import nitchcorp.backend.titan.events.domain.enums.TicketStatus;
import nitchcorp.backend.titan.events.domain.enums.TypeTicket;
import nitchcorp.backend.titan.shared.minio.service.MinioService;
import nitchcorp.backend.titan.shared.minio.enums.FileType;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.utils.constantSecurities.TypeRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Tag(name = "QR Code Test", description = "API de test pour la génération de QR codes")
@RestController
@RequestMapping("/api/test/qrcode")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class QrCodeTestController {

    private final QrCodeService qrCodeService;
    private final MinioService minioService;

    @PostMapping("/generate")
    @Operation(summary = "Générer un QR code de test", description = "Génère un QR code avec des données de test et l'upload sur MinIO")
    @ApiResponse(responseCode = "200", description = "QR code généré avec succès")
    @ApiResponse(responseCode = "500", description = "Erreur lors de la génération")
    public ResponseEntity<Map<String, Object>> generateTestQrCode(
            @Parameter(description = "Nom de l'acheteur", example = "John Doe")
            @RequestParam(defaultValue = "John Doe") String buyerName,
            @Parameter(description = "Nom de l'événement", example = "Concert Test")
            @RequestParam(defaultValue = "Concert Test") String eventName,
            @Parameter(description = "Nombre de tickets", example = "2")
            @RequestParam(defaultValue = "2") int numberOfTickets,
            @Parameter(description = "Prix du ticket", example = "50.0")
            @RequestParam(defaultValue = "50.0") double ticketPrice) {

        log.info("Génération QR code de test pour: {}, événement: {}", buyerName, eventName);

        try {
            // Créer des données de test
            PurchasedTicket testTicket = createTestPurchasedTicket(buyerName, eventName, numberOfTickets, ticketPrice);

            // Générer le QR code et l'uploader
            String qrCodeUrl = qrCodeService.generateAndUploadQrCode(testTicket);

            // Générer le code voucher
            String voucherCode = qrCodeService.generateVoucherCode(testTicket);

            // Préparer la réponse
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrCodeUrl", qrCodeUrl);
            response.put("voucherCode", voucherCode);
            response.put("ticketTrackingId", testTicket.getTicketTrackingId());
            response.put("buyerName", testTicket.getBuyer().getFirstName() + " " + testTicket.getBuyer().getLastName());
            response.put("eventName", testTicket.getTicketTemplate().getEvent().getName());
            response.put("numberOfTickets", testTicket.getNumberOfTicketsBought());
            response.put("ticketPrice", testTicket.getTicketTemplate().getPrice());
            response.put("status", testTicket.getStatus());

            log.info("QR code de test généré avec succès: {}", qrCodeUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code de test: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/generate-complete")
    @Operation(summary = "Générer codes complets de test", description = "Génère QR code et voucher code en une seule opération")
    @ApiResponse(responseCode = "200", description = "Codes générés avec succès")
    public ResponseEntity<Map<String, Object>> generateCompleteTestCodes(
            @Parameter(description = "Nom de l'acheteur", example = "Jane Smith")
            @RequestParam(defaultValue = "Jane Smith") String buyerName,
            @Parameter(description = "Nom de l'événement", example = "Festival Test")
            @RequestParam(defaultValue = "Festival Test") String eventName) {

        log.info("Génération codes complets de test pour: {}, événement: {}", buyerName, eventName);

        try {
            PurchasedTicket testTicket = createTestPurchasedTicket(buyerName, eventName, 1, 75.0);

            // Générer les codes complets
            QrCodeServiceImpl.TicketCodesResult result = qrCodeService.generateTicketCodes(testTicket);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrCodeUrl", result.getQrCodeUrl());
            response.put("voucherCode", result.getVoucherCode());
            response.put("ticketTrackingId", testTicket.getTicketTrackingId());
            response.put("generatedAt", LocalDateTime.now());

            log.info("Codes complets générés avec succès");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur génération codes complets: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/display/{fileName}")
    @Operation(summary = "Afficher un QR code", description = "Affiche le contenu binaire d'un QR code depuis MinIO")
    @ApiResponse(responseCode = "200", description = "Image retournée avec succès")
    @ApiResponse(responseCode = "404", description = "QR code non trouvé")
    public ResponseEntity<byte[]> displayQrCode(
            @Parameter(description = "Nom du fichier QR code", required = true)
            @PathVariable String fileName) {

        log.info("Affichage QR code: {}", fileName);

        try {
            // Récupérer le contenu du fichier depuis MinIO
            byte[] qrCodeBytes = minioService.getFileContentAsBytes(fileName, FileType.IMAGE);

            // Récupérer les métadonnées pour le Content-Type
            var metadata = minioService.getFileMetadata(fileName, FileType.IMAGE);
            String contentType = metadata.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "image/png";
            }

            log.info("QR code affiché avec succès: {} (taille: {} bytes)", fileName, qrCodeBytes.length);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(qrCodeBytes.length)
                    .body(qrCodeBytes);

        } catch (Exception e) {
            log.error("Erreur affichage QR code {}: {}", fileName, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Supprimer un QR code de test", description = "Supprime un QR code depuis MinIO")
    @ApiResponse(responseCode = "200", description = "QR code supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "QR code non trouvé")
    public ResponseEntity<Map<String, Object>> deleteQrCode(
            @Parameter(description = "URL du QR code à supprimer", required = true)
            @RequestParam String qrCodeUrl) {

        log.info("Suppression QR code: {}", qrCodeUrl);

        try {
            qrCodeService.deleteQrCode(qrCodeUrl);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "QR code supprimé avec succès");
            response.put("deletedUrl", qrCodeUrl);

            log.info("QR code supprimé avec succès: {}", qrCodeUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur suppression QR code {}: {}", qrCodeUrl, e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/variants")
    @Operation(summary = "Obtenir les variants d'un QR code", description = "Retourne les différentes tailles d'un QR code (simulation)")
    @ApiResponse(responseCode = "200", description = "Variants récupérés avec succès")
    public ResponseEntity<Map<String, Object>> getQrCodeVariants(
            @Parameter(description = "URL du QR code original", required = true)
            @RequestParam String originalUrl) {

        log.info("Récupération variants QR code: {}", originalUrl);

        try {
            QrCodeServiceImpl.QrCodeVariants variants = qrCodeService.getQrCodeVariants(originalUrl);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("thumbnail", variants.getThumbnail());
            response.put("small", variants.getSmall());
            response.put("medium", variants.getMedium());
            response.put("large", variants.getLarge());
            response.put("original", variants.getOriginal());
            response.put("note", "MinIO ne supporte pas les transformations d'images - même URL pour tous les variants");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur récupération variants: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Crée un PurchasedTicket de test avec des données mockées
     */
    private PurchasedTicket createTestPurchasedTicket(String buyerName, String eventName, int numberOfTickets, double price) {
        // Créer un utilisateur de test
        User testBuyer = new User();
        testBuyer.setTrackingId(UUID.randomUUID());
        testBuyer.setFirstName(buyerName.split(" ")[0]);
        testBuyer.setLastName(buyerName.contains(" ") ? buyerName.split(" ")[1] : "Test");
        testBuyer.setEmail("test@example.com");
        testBuyer.setPhone("1234567890");
        testBuyer.setPassword("password");
        testBuyer.setRole(TypeRole.USER);
        testBuyer.setActif(true);

        // Créer un utilisateur organisateur de test
        User testOrganizer = new User();
        testOrganizer.setTrackingId(UUID.randomUUID());
        testOrganizer.setFirstName("Test");
        testOrganizer.setLastName("Organizer");
        testOrganizer.setEmail("organizer@example.com");
        testOrganizer.setPhone("0987654321");
        testOrganizer.setPassword("password");
        testOrganizer.setRole(TypeRole.ADMIN);
        testOrganizer.setActif(true);

        // Créer un événement de test
        Events testEvent = Events.builder()
                .trackingId(UUID.randomUUID())
                .name(eventName)
                .description("Événement de test pour génération QR code")
                .capacity(1000)
                .startDateTime(LocalDateTime.now().plusDays(30))
                .endDateTime(LocalDateTime.now().plusDays(30).plusHours(4))
                .organizer(testOrganizer)
                .build();

        // Créer un template de ticket de test
        TicketTemplate testTemplate = TicketTemplate.builder()
                .trackingId(UUID.randomUUID())
                .type(TypeTicket.VIP)
                .price(price)
                .numberOfAvailableTickets(100)
                .numberOfTicketsSold(10)
                .event(testEvent)
                .creator(testOrganizer)
                .build();

        // Créer le ticket acheté de test
        return PurchasedTicket.builder()
                .ticketTrackingId(UUID.randomUUID())
                .buyer(testBuyer)
                .numberOfTicketsBought(numberOfTickets)
                .status(TicketStatus.ACTIVE)
                .ticketTemplate(testTemplate)
                .build();
    }
}
