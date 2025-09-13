package nitchcorp.backend.titan.immo.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import nitchcorp.backend.titan.immo.domain.enums.PaiementStatus;
import nitchcorp.backend.titan.immo.domain.enums.PaiementType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaiementRequest(
        @NotNull(message = "L'ID de suivi du contrat de location est requis")
        @JsonProperty("leaseContractTrackingId")
        UUID leaseContractTrackingId,
        
        @NotNull(message = "L'ID de suivi du client est requis")
        @JsonProperty("customerTrackingId")
        UUID customerTrackingId,
        
        @NotNull(message = "Le montant du paiement est requis")
        @JsonProperty("amount")
        BigDecimal amount,
        
        @NotNull(message = "Le type de paiement est requis")
        @JsonProperty("paymentType")
        PaiementType paymentType,
        
        @JsonProperty("paymentDate")
        LocalDateTime paymentDate,
        
        @NotNull(message = "La date d'échéance est requise")
        @JsonProperty("dueDate")
        LocalDate dueDate,
        
        @NotNull(message = "Le statut du paiement est requis")
        @JsonProperty("status")
        PaiementStatus status,
        
        @NotNull(message = "L'ID de transaction est requis")
        @JsonProperty("transactionId")
        String transactionId
) {}
