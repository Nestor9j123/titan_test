package nitchcorp.backend.titan.immo.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.immo.application.dto.requests.CustomerRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.CustomerResponse;
import nitchcorp.backend.titan.immo.domain.exceptions.UserNotFoundException;
import nitchcorp.backend.titan.immo.application.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Customers", description = "API pour la gestion des clients")
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/create")
    @Operation(summary = "Créer un nouveau client", description = "Crée un nouveau client dans le système")
    @ApiResponse(responseCode = "201", description = "Client créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<CustomerResponse> createCustomer(
            @Parameter(description = "Informations du client à créer", required = true)
            @Valid @RequestBody CustomerRequest request) {
        log.info("Creating customer with email: {} at 11:19 AM GMT, 20/08/2025", request.email());
        try {
            CustomerResponse response = customerService.createCustomer(request);
            log.info("Customer created successfully with trackingId: {}", response.trackingId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for creating customer: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error creating customer: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Récupérer tous les clients", description = "Récupère la liste de tous les clients")
    @ApiResponse(responseCode = "200", description = "Liste des clients récupérée avec succès")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        log.info("Fetching all customers at 11:19 AM GMT, 20/08/2025");
        try {
            List<CustomerResponse> customers = customerService.getAllCustomers();
            log.info("Retrieved {} customers", customers.size());
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            log.error("Error fetching all customers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer un client par ID de suivi", description = "Récupère un client spécifique par son ID de suivi")
    @ApiResponse(responseCode = "200", description = "Client trouvé")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    public ResponseEntity<CustomerResponse> getCustomerByTrackingId(
            @Parameter(description = "ID de suivi du client", required = true)
            @PathVariable UUID trackingId) {
        log.info("Fetching customer with trackingId: {} at 11:19 AM GMT, 20/08/2025", trackingId);
        try {
            CustomerResponse customer = customerService.getCustomerByTrackingId(trackingId);
            log.info("Customer found with trackingId: {}", trackingId);
            return ResponseEntity.ok(customer);
        } catch (UserNotFoundException e) {
            log.error("Customer not found with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{trackingId}")
    @Operation(summary = "Mettre à jour un client", description = "Met à jour les informations d'un client existant")
    @ApiResponse(responseCode = "200", description = "Client mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @Parameter(description = "ID de suivi du client", required = true)
            @PathVariable UUID trackingId,
            @Parameter(description = "Nouvelles informations du client", required = true)
            @Valid @RequestBody CustomerRequest request) {
        log.info("Updating customer with trackingId: {} at 11:19 AM GMT, 20/08/2025", trackingId);
        try {
            CustomerResponse updatedCustomer = customerService.updateCustomer(trackingId, request);
            log.info("Customer updated successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok(updatedCustomer);
        } catch (UserNotFoundException e) {
            log.error("Customer not found with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for updating customer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{trackingId}")
    @Operation(summary = "Supprimer un client", description = "Supprime un client du système")
    @ApiResponse(responseCode = "200", description = "Client supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    public ResponseEntity<String> deleteCustomer(
            @Parameter(description = "ID de suivi du client", required = true)
            @PathVariable UUID trackingId) {
        log.info("Deleting customer with trackingId: {} at 11:19 AM GMT, 20/08/2025", trackingId);
        try {
            customerService.deleteCustomer(trackingId);
            log.info("Customer deleted successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok("Client supprimé avec succès");
        } catch (UserNotFoundException e) {
            log.error("Customer not found with trackingId: {}", trackingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client non trouvé");
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId for deletion: {}", e.getMessage());
            return ResponseEntity.badRequest().body("ID de tracking invalide");
        }
    }
}