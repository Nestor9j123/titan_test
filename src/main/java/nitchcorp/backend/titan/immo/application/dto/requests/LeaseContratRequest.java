package nitchcorp.backend.titan.immo.application.dto.requests;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import nitchcorp.backend.titan.immo.domain.enums.LeaseStatus;
import org.springframework.modulith.NamedInterface;

import java.math.BigDecimal;
import java.time.LocalDate;

@NamedInterface
@Builder
public record LeaseContratRequest(
        @JsonProperty("propertyId")
        @NotNull(message = "L'ID de la propriété est requis")
        Long propertyId,
        @JsonProperty("customerId")
        @NotNull(message = "L'ID du client est requis")
        Long customerId,
        @JsonProperty("agentId")
        @NotNull(message = "L'ID de l'agent est requis")
        Long agentId,
        @JsonProperty("startDate")
        @NotNull(message = "La date de début du contrat est requise")
        LocalDate startDate,
        @JsonProperty("endDate")
        @NotNull(message = "La date de fin du contrat est requise")
        LocalDate endDate,
        @JsonProperty("rentAmount")
        @NotNull(message = "Le montant du loyer est requis")
        BigDecimal rentAmount,
        @JsonProperty("depositAmount")
        @NotNull(message = "Le montant du dépôt est requis")
        BigDecimal depositAmount,
        @JsonProperty("contractDocument")
        String contractDocument,
        @JsonProperty("status")
        @NotNull(message = "Le statut du contrat est requis")
        LeaseStatus status
) {}
