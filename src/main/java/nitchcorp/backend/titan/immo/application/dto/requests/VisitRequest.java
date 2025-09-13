package nitchcorp.backend.titan.immo.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import nitchcorp.backend.titan.immo.domain.enums.VisitStatus;

import java.time.LocalDateTime;

public record VisitRequest(
        @JsonProperty("propertyId") @NotNull(message = "L'ID de la propriété est requis") Long propertyId,
        @JsonProperty("agentId") @NotNull(message = "L'ID de l'agent est requis") Long agentId,
        @JsonProperty("customerId") @NotNull(message = "L'ID du client est requis") Long customerId,
        @JsonProperty("visitDate") @NotNull(message = "La date de la visite est requise") LocalDateTime visitDate,
        @JsonProperty("status") @NotNull(message = "Le statut de la visite est requis") VisitStatus status,
        @JsonProperty("comments") String comments
) {}
