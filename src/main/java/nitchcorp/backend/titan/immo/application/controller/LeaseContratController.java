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
import nitchcorp.backend.titan.immo.application.dto.requests.LeaseContratRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.LeaseContratResponse;
import nitchcorp.backend.titan.immo.domain.exceptions.LeaseContratNotFoundException;
import nitchcorp.backend.titan.immo.application.service.LeaseContratService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "LeaseContratController", description = "API pour LeaseContratController")
@RestController
@RequestMapping("/api/lease-contracts")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class LeaseContratController {

    private final LeaseContratService leaseContratService;

    @PostMapping("/create")
    public ResponseEntity<LeaseContratResponse> createLeaseContrat(@Valid @RequestBody LeaseContratRequest request) {
        log.info("Received request: propertyId={}, customerId={}, agentId={}",
                request.propertyId(), request.customerId(), request.agentId());

        if (request.propertyId() == null || request.customerId() == null || request.agentId() == null) {
            log.error("Missing required IDs in the request. Full request: {}", request);
            return ResponseEntity.badRequest().body(null);
        }

        log.info("Creating lease contract for property: {}", request.propertyId());

        try {
            LeaseContratResponse response = leaseContratService.createLeaseContrat(request);
            log.info("Lease contract created successfully with ID: {}", response.trackingId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (LeaseContratNotFoundException e) {
            log.error("Lease contract creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for creating lease contract: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error creating lease contract: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<LeaseContratResponse>> getAllLeaseContrats() {
        log.info("Fetching all lease contracts");
        try {
            List<LeaseContratResponse> leaseContrats = leaseContratService.getAllLeaseContrats();
            log.info("Retrieved {} lease contracts", leaseContrats.size());
            return ResponseEntity.ok(leaseContrats);
        } catch (Exception e) {
            log.error("Error fetching all lease contracts: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<LeaseContratResponse> getLeaseContratByTrackingId(@PathVariable UUID trackingId) {
        log.info("Fetching lease contract with trackingId: {}", trackingId);
        try {
            LeaseContratResponse leaseContrat = leaseContratService.getLeaseContratByTrackingId(trackingId);
            log.info("Lease contract found with trackingId: {}", trackingId);
            return ResponseEntity.ok(leaseContrat);
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Lease contract not found with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Mettre à jour un contrat existant
     * @param trackingId L'ID de tracking du contrat à mettre à jour
     * @param request Les nouvelles données du contrat
     * @return Le contrat mis à jour
     */
    @PutMapping("/update/{trackingId}")
    public ResponseEntity<LeaseContratResponse> updateLeaseContrat(
            @PathVariable UUID trackingId, @Valid @RequestBody LeaseContratRequest request) {
        log.info("Updating lease contract with trackingId: {}", trackingId);
        try {
            LeaseContratResponse updatedLeaseContrat = leaseContratService.updateLeaseContrat(trackingId, request);
            log.info("Lease contract updated successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok(updatedLeaseContrat);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for updating lease contract: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating lease contract with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer un contrat
     * @param trackingId L'ID de tracking du contrat à supprimer
     * @return Confirmation de suppression
     */
    @DeleteMapping("/delete/{trackingId}")
    public ResponseEntity<String> deleteLeaseContrat(@PathVariable UUID trackingId) {
        log.info("Deleting lease contract with trackingId: {}", trackingId);
        try {
            leaseContratService.deleteLeaseContrat(trackingId);
            log.info("Lease contract deleted successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok("Contrat supprimé avec succès");
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId for deletion: {}", e.getMessage());
            return ResponseEntity.badRequest().body("ID de tracking invalide");
        } catch (Exception e) {
            log.error("Error deleting lease contract with trackingId: {}", trackingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrat non trouvé");
        }
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<String> deleteLeaseContratRestful(@PathVariable UUID trackingId) {
        return deleteLeaseContrat(trackingId);
    }

    // Endpoints manquants selon le cahier des charges

    @GetMapping("/active/property/{propertyId}")
    public ResponseEntity<List<LeaseContratResponse>> getLeaseContratsByProperty(@PathVariable UUID propertyId) {
        log.info("Fetching lease contracts for property: {}", propertyId);
        try {
            List<LeaseContratResponse> contracts = leaseContratService.getLeaseContratsByProperty(propertyId);
            log.info("Retrieved {} lease contracts for property", contracts.size());
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            log.error("Error fetching lease contracts by property: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LeaseContratResponse>> getLeaseContratsByCustomer(@PathVariable UUID customerId) {
        log.info("Fetching lease contracts for customer: {}", customerId);
        try {
            List<LeaseContratResponse> contracts = leaseContratService.getLeaseContratsByCustomer(customerId);
            log.info("Retrieved {} lease contracts for customer", contracts.size());
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            log.error("Error fetching lease contracts by customer: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<LeaseContratResponse>> getLeaseContratsByOwner(@PathVariable UUID ownerId) {
        log.info("Fetching lease contracts for owner: {}", ownerId);
        try {
            List<LeaseContratResponse> contracts = leaseContratService.getLeaseContratsByOwner(ownerId);
            log.info("Retrieved {} lease contracts for owner", contracts.size());
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            log.error("Error fetching lease contracts by owner: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<LeaseContratResponse>> getLeaseContratsByAgent(@PathVariable UUID agentId) {
        log.info("Fetching lease contracts for agent: {}", agentId);
        try {
            List<LeaseContratResponse> contracts = leaseContratService.getLeaseContratsByAgent(agentId);
            log.info("Retrieved {} lease contracts for agent", contracts.size());
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            log.error("Error fetching lease contracts by agent: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<LeaseContratResponse>> getActiveLeaseContrats() {
        log.info("Fetching active lease contracts");
        try {
            List<LeaseContratResponse> contracts = leaseContratService.getActiveLeaseContracts();
            log.info("Retrieved {} active lease contracts", contracts.size());
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            log.error("Error fetching active lease contracts: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<LeaseContratResponse>> getExpiringLeaseContrats(@RequestParam(defaultValue = "30") int days) {
        log.info("Fetching lease contracts expiring in {} days", days);
        try {
            List<LeaseContratResponse> contracts = leaseContratService.getExpiringLeaseContracts(days);
            log.info("Retrieved {} expiring lease contracts", contracts.size());
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            log.error("Error fetching expiring lease contracts: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{trackingId}/sign")
    public ResponseEntity<LeaseContratResponse> signLeaseContract(@PathVariable UUID trackingId, @RequestParam String signerType) {
        log.info("Signing lease contract: {}", trackingId);
        try {
            LeaseContratResponse contract = leaseContratService.signLeaseContract(trackingId, signerType);
            log.info("Lease contract signed successfully");
            return ResponseEntity.ok(contract);
        } catch (Exception e) {
            log.error("Error signing lease contract: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{trackingId}/terminate")
    public ResponseEntity<LeaseContratResponse> terminateLeaseContract(
            @PathVariable UUID trackingId, 
            @RequestParam String terminationDate,
            @RequestParam(required = false) String reason) {
        log.info("Terminating lease contract: {} on {} for reason: {}", trackingId, terminationDate, reason);
        try {
            LeaseContratResponse contract = leaseContratService.terminateLeaseContract(trackingId, reason, terminationDate);
            log.info("Lease contract termination initiated successfully");
            return ResponseEntity.ok(contract);
        } catch (Exception e) {
            log.error("Error terminating lease contract: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{trackingId}/renew")
    public ResponseEntity<LeaseContratResponse> renewLeaseContract(
            @PathVariable UUID trackingId,
            @RequestParam String newEndDate,
            @RequestParam(required = false) Double newRentAmount) {
        log.info("Renewing lease contract: {} until {} with new rent: {}", trackingId, newEndDate, newRentAmount);
        try {
            LeaseContratResponse contract = leaseContratService.renewLeaseContract(trackingId, newEndDate, newRentAmount);
            log.info("Lease contract renewed successfully");
            return ResponseEntity.ok(contract);
        } catch (Exception e) {
            log.error("Error renewing lease contract: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{trackingId}/confirm-deposit")
    public ResponseEntity<LeaseContratResponse> confirmDepositPayment(@PathVariable UUID trackingId, @RequestParam Double depositAmount) {
        log.info("Confirming deposit payment for lease contract: {}", trackingId);
        try {
            LeaseContratResponse contract = leaseContratService.confirmDeposit(trackingId, depositAmount);
            log.info("Deposit payment confirmed successfully");
            return ResponseEntity.ok(contract);
        } catch (Exception e) {
            log.error("Error confirming deposit payment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{trackingId}/pdf")
    public ResponseEntity<byte[]> generateContractPdf(@PathVariable UUID trackingId) {
        log.info("Generating PDF for lease contract: {}", trackingId);
        try {
            byte[] pdfBytes = leaseContratService.generateLeaseContractPdf(trackingId);
            log.info("PDF generated successfully");
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=contract-" + trackingId + ".pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating contract PDF: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    // @Operation(summary = "Historique des contrats", description = "Récupère l'historique des contrats pour une période")
    // @GetMapping("/history")
    // public ResponseEntity<List<LeaseContratResponse>> getContractHistory(
    //         @RequestParam(required = false) String startDate,
    //         @RequestParam(required = false) String endDate,
    //         @RequestParam(required = false) String status) {
    //     log.info("Fetching contract history from {} to {} with status: {}", startDate, endDate, status);
    //     try {
    //         List<LeaseContratResponse> contracts = leaseContratService.getContractHistory(startDate, endDate, status);
    //         log.info("Retrieved {} contracts in history", contracts.size());
    //         return ResponseEntity.ok(contracts);
    //     } catch (Exception e) {
    //         log.error("Error fetching contract history: {}", e.getMessage(), e);
    //         return ResponseEntity.badRequest().build();
    //     }
    // }
}
