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
import nitchcorp.backend.titan.immo.application.dto.requests.PaiementRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.PaiementResponse;
import nitchcorp.backend.titan.immo.domain.exceptions.PaiementNotFoundException;
import nitchcorp.backend.titan.immo.application.service.PaiementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Tag(name = "PaiementController", description = "API pour PaiementController")
@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping("/create")
    public ResponseEntity<PaiementResponse> createPaiement(@Valid @RequestBody PaiementRequest request) {
        log.info("Received request: leaseContractTrackingId={}, customerTrackingId={}, amount={}",
                request.leaseContractTrackingId(), request.customerTrackingId(), request.amount());

        if (request.leaseContractTrackingId() == null || request.customerTrackingId() == null || request.amount() == null) {
            log.error("Missing required fields in the request. Full request: {}", request);
            return ResponseEntity.badRequest().body(null);
        }

        log.info("Creating paiement for lease contract: {}", request.leaseContractTrackingId());

        try {
            PaiementResponse response = paiementService.createPaiement(request);
            log.info("Paiement created successfully with ID: {}", response.trackingId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (PaiementNotFoundException e) {
            log.error("Paiement creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for creating paiement: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error creating paiement: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<PaiementResponse>> getAllPaiements() {
        log.info("Fetching all paiements");
        try {
            List<PaiementResponse> paiements = paiementService.getAllPaiements();
            log.info("Retrieved {} paiements", paiements.size());
            return ResponseEntity.ok(paiements);
        } catch (Exception e) {
            log.error("Error fetching all paiements: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<PaiementResponse> getPaiementByTrackingId(@PathVariable UUID trackingId) {
        log.info("Fetching paiement with trackingId: {}", trackingId);
        try {
            PaiementResponse paiement = paiementService.getPaiementByTrackingId(trackingId);
            log.info("Paiement found with trackingId: {}", trackingId);
            return ResponseEntity.ok(paiement);
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Paiement not found with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{trackingId}")
    public ResponseEntity<PaiementResponse> updatePaiement(
            @PathVariable UUID trackingId, @Valid @RequestBody PaiementRequest request) {
        log.info("Updating paiement with trackingId: {}", trackingId);
        try {
            PaiementResponse updatedPaiement = paiementService.updatePaiement(trackingId, request);
            log.info("Paiement updated successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok(updatedPaiement);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for updating paiement: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating paiement with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{trackingId}")
    public ResponseEntity<String> deletePaiement(@PathVariable UUID trackingId) {
        log.info("Deleting paiement with trackingId: {}", trackingId);
        try {
            paiementService.deletePaiement(trackingId);
            log.info("Paiement deleted successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok("Paiement supprimé avec succès");
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId for deletion: {}", e.getMessage());
            return ResponseEntity.badRequest().body("ID de tracking invalide");
        } catch (Exception e) {
            log.error("Error deleting paiement with trackingId: {}", trackingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paiement non trouvé");
        }
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<String> deletePaiementRestful(@PathVariable UUID trackingId) {
        return deletePaiement(trackingId);
    }

    // Endpoints manquants selon le cahier des charges

    @GetMapping("/lease-contract/{leaseContractTrackingId}")
    public ResponseEntity<List<PaiementResponse>> getPaiementsByLeaseContract(@PathVariable UUID leaseContractId) {
        log.info("Fetching payments for lease contract: {}", leaseContractId);
        try {
            List<PaiementResponse> paiements = paiementService.getPaiementsByLeaseContract(leaseContractId);
            log.info("Retrieved {} payments for lease contract", paiements.size());
            return ResponseEntity.ok(paiements);
        } catch (Exception e) {
            log.error("Error fetching payments by lease contract: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customer/{customerTrackingId}")
    public ResponseEntity<List<PaiementResponse>> getPaiementsByCustomer(@PathVariable UUID customerId) {
        log.info("Fetching payments for customer: {}", customerId);
        try {
            List<PaiementResponse> paiements = paiementService.getPaiementsByCustomer(customerId);
            log.info("Retrieved {} payments for customer", paiements.size());
            return ResponseEntity.ok(paiements);
        } catch (Exception e) {
            log.error("Error fetching payments by customer: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/owner/{ownerTrackingId}")
    public ResponseEntity<List<PaiementResponse>> getPaiementsByOwner(@PathVariable UUID ownerId) {
        log.info("Fetching payments for owner: {}", ownerId);
        try {
            List<PaiementResponse> paiements = paiementService.getPaiementsByOwner(ownerId);
            log.info("Retrieved {} payments for owner", paiements.size());
            return ResponseEntity.ok(paiements);
        } catch (Exception e) {
            log.error("Error fetching payments by owner: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<PaiementResponse>> getOverduePayments() {
        log.info("Fetching overdue payments");
        try {
            List<PaiementResponse> paiements = paiementService.getOverduePayments();
            log.info("Retrieved {} overdue payments", paiements.size());
            return ResponseEntity.ok(paiements);
        } catch (Exception e) {
            log.error("Error fetching overdue payments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaiementResponse>> getPaiementsByStatus(@PathVariable String status) {
        log.info("Fetching payments with status: {}", status);
        try {
            List<PaiementResponse> paiements = paiementService.getPaiementsByStatus(status);
            log.info("Retrieved {} payments with status: {}", paiements.size(), status);
            return ResponseEntity.ok(paiements);
        } catch (Exception e) {
            log.error("Error fetching payments by status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{trackingId}/mark-paid")
    public ResponseEntity<PaiementResponse> confirmPayment(@PathVariable UUID trackingId) {
        log.info("Confirming payment: {}", trackingId);
        try {
            PaiementResponse paiement = paiementService.confirmPayment(trackingId);
            log.info("Payment confirmed successfully");
            return ResponseEntity.ok(paiement);
        } catch (Exception e) {
            log.error("Error confirming payment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{trackingId}/mark-refunded")
    public ResponseEntity<PaiementResponse> markPaymentOverdue(@PathVariable UUID trackingId) {
        log.info("Marking payment as overdue: {}", trackingId);
        try {
            PaiementResponse paiement = paiementService.markPaymentOverdue(trackingId);
            log.info("Payment marked as overdue successfully");
            return ResponseEntity.ok(paiement);
        } catch (Exception e) {
            log.error("Error marking payment as overdue: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{trackingId}/reminder")
    public ResponseEntity<String> generatePaymentReminder(@PathVariable UUID trackingId) {
        log.info("Generating payment reminder for: {}", trackingId);
        try {
            paiementService.generatePaymentReminder(trackingId);
            log.info("Payment reminder generated successfully");
            return ResponseEntity.ok("Rappel de paiement généré avec succès");
        } catch (Exception e) {
            log.error("Error generating payment reminder: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur lors de la génération du rappel");
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<PaiementResponse>> getPaymentHistory(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) UUID ownerId) {
        log.info("Fetching payment history from {} to {} for customer: {}, owner: {}", startDate, endDate, customerId, ownerId);
        try {
            List<PaiementResponse> paiements = paiementService.getPaymentHistory(startDate, endDate, customerId, ownerId);
            log.info("Retrieved {} payments in history", paiements.size());
            return ResponseEntity.ok(paiements);
        } catch (Exception e) {
            log.error("Error fetching payment history: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/owner/{ownerId}/stats")
    public ResponseEntity<Map<String, Object>> getPaymentStats(@PathVariable UUID ownerId) {
        log.info("Fetching payment statistics for owner: {}", ownerId);
        try {
            Map<String, Object> stats = paiementService.getPaymentStats(ownerId);
            log.info("Retrieved payment statistics");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching payment statistics: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/lease/{leaseContractId}/auto-payment")
    public ResponseEntity<String> setupAutoPayment(@PathVariable UUID leaseContractId, @RequestParam boolean enabled) {
        log.info("Setting up auto payment for lease contract: {}, enabled: {}", leaseContractId, enabled);
        try {
            paiementService.setupAutoPayment(leaseContractId, enabled);
            String message = enabled ? "Paiement automatique activé" : "Paiement automatique désactivé";
            log.info("Auto payment setup completed");
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            log.error("Error setting up auto payment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur lors de la configuration du paiement automatique");
        }
    }
}
