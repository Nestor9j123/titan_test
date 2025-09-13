package nitchcorp.backend.titan.events.Application.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.events.Application.dtos.request.PurchaseTicketRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.PurchasedTicketResponse;
import nitchcorp.backend.titan.events.domain.enums.TicketStatus;
import nitchcorp.backend.titan.events.Application.service.ImplService.PurchasedTicketServiceImpl;
import nitchcorp.backend.titan.shared.utils.constantSecurities.SecurityConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Votes", description = "API pour la gestion des  ticket payé")
@RestController
@RequestMapping("/api/purchased_ticket")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class PurchasedTicketController {

    private final PurchasedTicketServiceImpl purchasedTicketService;

    @Operation(
            summary = "Acheter un ticket",
            description = "Effectue l'achat d'un ticket pour un événement"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Ticket acheté avec succès",
            content = @Content(schema = @Schema(implementation = PurchasedTicketResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Template de ticket non trouvé")
    @ApiResponse(responseCode = "409", description = "Ticket non disponible")
    @PostMapping("/purchase")
    public ResponseEntity<PurchasedTicketResponse> purchaseTicket(
            @RequestBody @Valid PurchaseTicketRequest request
    ) {
        PurchasedTicketResponse ticket = purchasedTicketService.purchaseTicket(request);
        return new ResponseEntity<>(ticket, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Récupérer tous les tickets achetés",
            description = "Récupère la liste complète de tous les tickets achetés"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des tickets achetés récupérée avec succès",
            content = @Content(schema = @Schema(implementation = PurchasedTicketResponse.class))
    )
    @GetMapping
    public ResponseEntity<List<PurchasedTicketResponse>> getAllPurchasedTickets() {
        List<PurchasedTicketResponse> tickets = purchasedTicketService.getAllPurchasedTickets();
        return ResponseEntity.ok(tickets);
    }

    @Operation(
            summary = "Récupérer les tickets achetés d'un événement",
            description = "Récupère tous les tickets achetés pour un événement spécifique"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Tickets de l'événement récupérés avec succès",
            content = @Content(schema = @Schema(implementation = PurchasedTicketResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    @GetMapping("/event/{eventTrackingId}")
    public ResponseEntity<List<PurchasedTicketResponse>> getPurchasedTicketsByEvent(@PathVariable UUID eventTrackingId) {
        List<PurchasedTicketResponse> tickets = purchasedTicketService.getPurchasedTicketsByEvent(eventTrackingId);
        return ResponseEntity.ok(tickets);
    }

    @Operation(
            summary = "Récupérer les tickets achetés par un acheteur",
            description = "Récupère tous les tickets achetés par un acheteur spécifique"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Tickets de l'acheteur récupérés avec succès",
            content = @Content(schema = @Schema(implementation = PurchasedTicketResponse.class))
    )
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<PurchasedTicketResponse>> getPurchasedTicketsByBuyer(@PathVariable UUID buyerId) {
        List<PurchasedTicketResponse> tickets = purchasedTicketService.getPurchasedTicketsByBuyer(buyerId);
        return ResponseEntity.ok(tickets);
    }

    @Operation(
            summary = "Récupérer un ticket acheté par son ID",
            description = "Récupère un ticket acheté spécifique par son tracking ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Ticket acheté récupéré avec succès",
            content = @Content(schema = @Schema(implementation = PurchasedTicketResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Ticket non trouvé")
    @GetMapping("/{ticketTrackingId}")
    public ResponseEntity<PurchasedTicketResponse> getPurchasedTicketById(@PathVariable UUID ticketTrackingId) {
        PurchasedTicketResponse ticket = purchasedTicketService.getPurchasedTicketByTrackingId(ticketTrackingId);
        return ResponseEntity.ok(ticket);
    }

    @Operation(
            summary = "Mettre à jour le statut d'un ticket",
            description = "Met à jour le statut d'un ticket acheté (ACTIVE, USED, CANCELLED, etc.)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Statut du ticket mis à jour avec succès",
            content = @Content(schema = @Schema(implementation = PurchasedTicketResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Ticket non trouvé")
    @ApiResponse(responseCode = "400", description = "Statut invalide")
    @PatchMapping("/{ticketTrackingId}/status")
    public ResponseEntity<PurchasedTicketResponse> updateTicketStatus(
            @PathVariable UUID ticketTrackingId,
            @RequestParam TicketStatus status) {
        PurchasedTicketResponse ticket = purchasedTicketService.updateTicketStatus(ticketTrackingId, status);
        return ResponseEntity.ok(ticket);
    }

    @Operation(
            summary = "Annuler un ticket",
            description = "Annule un ticket acheté et le supprime du système"
    )
    @ApiResponse(responseCode = "204", description = "Ticket annulé avec succès")
    @ApiResponse(responseCode = "404", description = "Ticket non trouvé")
    @ApiResponse(responseCode = "409", description = "Ticket ne peut pas être annulé (déjà utilisé, etc.)")
    @DeleteMapping("/{ticketTrackingId}")
    public ResponseEntity<Void> cancelTicket(@PathVariable UUID ticketTrackingId) {
        purchasedTicketService.cancelTicket(ticketTrackingId);
        return ResponseEntity.noContent().build();
    }



    @Operation(
            summary = "Valider un ticket par code voucher",
            description = "Valide un ticket acheté en utilisant son code voucher et le marque comme utilisé"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Ticket validé avec succès par code voucher",
            content = @Content(schema = @Schema(implementation = PurchasedTicketResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Ticket non trouvé avec ce code voucher")
    @ApiResponse(responseCode = "400", description = "Ticket déjà utilisé ou annulé")
    @PostMapping("/validate/voucher/{voucherCode}")
    public ResponseEntity<PurchasedTicketResponse> validateTicketByVoucher(@PathVariable String voucherCode) {
        PurchasedTicketResponse ticket = purchasedTicketService.validateTicketByVoucherCode(voucherCode);
        return ResponseEntity.ok(ticket);
    }

    @Operation(
            summary = "Valider un ticket par QR code",
            description = "Valide un ticket acheté en utilisant son tracking ID (extrait du QR code) et le marque comme utilisé"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Ticket validé avec succès par QR code",
            content = @Content(schema = @Schema(implementation = PurchasedTicketResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Ticket non trouvé")
    @ApiResponse(responseCode = "400", description = "Ticket déjà utilisé ou annulé")
    @PostMapping("/validate/qr/{ticketTrackingId}")
    public ResponseEntity<PurchasedTicketResponse> validateTicketByQrCode(@PathVariable UUID ticketTrackingId) {
        PurchasedTicketResponse ticket = purchasedTicketService.validateTicketByTrackingId(ticketTrackingId);
        return ResponseEntity.ok(ticket);
    }

    @Operation(
            summary = "Récupérer un ticket par code voucher",
            description = "Récupère un ticket acheté en utilisant son code voucher unique"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Ticket récupéré avec succès par code voucher",
            content = @Content(schema = @Schema(implementation = PurchasedTicketResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Ticket non trouvé avec ce code voucher")
    @GetMapping("/voucher/{voucherCode}")
    public ResponseEntity<PurchasedTicketResponse> getPurchasedTicketByVoucher(@PathVariable String voucherCode) {
        PurchasedTicketResponse ticket = purchasedTicketService.getPurchasedTicketByVoucherCode(voucherCode);
        return ResponseEntity.ok(ticket);
    }
}
