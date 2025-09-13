package nitchcorp.backend.titan.events.Application.dtos.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import nitchcorp.backend.titan.events.domain.enums.TicketStatus;
import nitchcorp.backend.titan.events.domain.enums.TypeTicket;

import java.util.UUID;

@Builder
public record PurchasedTicketResponse(
        @JsonProperty("ticketTrackingId")
        UUID ticketTrackingId,

        @JsonProperty("buyerName")
        String buyerName,

        @JsonProperty("nombreTicketAchete")
        int nombreTicketAchete,
        @JsonProperty("qrCodeUrl")
        String qrCodeUrl,

        @JsonProperty("voucherCode")
        String voucherCode,

        @JsonProperty("status")
        String status,

        @JsonProperty("type")
        String type,

        @JsonProperty("prixUnitaire")
        double prixUnitaire,

        @JsonProperty("prixTotal")
        double prixTotal,

        @JsonProperty("templateTrackingId")
        UUID templateTrackingId,

        @JsonProperty("eventTrackingId")
        UUID eventTrackingId
) {}