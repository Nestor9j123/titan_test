package nitchcorp.backend.titan.events.Application.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TicketTemplateResponse(
        @JsonProperty("trackingId")
        UUID trackingId,

        @JsonProperty("type")
        String type,

        @JsonProperty("price")
        double price,

        @JsonProperty("nombreTicketDisponible")
        int nombreTicketDisponible,

        @JsonProperty("nombreTicketVendu")
        int nombreTicketVendu,

        @JsonProperty("nombreTicketRestant")
        int nombreTicketRestant,

        @JsonProperty("isAvailable")
        boolean isAvailable,

        @JsonProperty("eventTrackingId")
        UUID eventTrackingId,

        @JsonProperty("creatorId")
        UUID creatorId
) {}