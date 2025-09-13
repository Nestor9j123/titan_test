package nitchcorp.backend.titan.immo.application.dto.requests;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record OwnerAgentAssignmentRequest(
        @JsonProperty("ownerId") @NotNull(message = "L'ID du propriétaire est requis") UUID ownerId,
        @JsonProperty("agentId") @NotNull(message = "L'ID de l'agent est requis") UUID agentId,
        @JsonProperty("propertyId") @NotNull(message = "L'ID de la propriété est requis") Long propertyId,
        @JsonProperty("assignedAt") @NotNull(message = "La date d'assignation est requise") LocalDateTime assignedAt,
        @JsonProperty("instructions") String instructions
) {}
