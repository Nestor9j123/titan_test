package nitchcorp.backend.titan.events.Application.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import nitchcorp.backend.titan.events.domain.enums.TypeTicket;

import java.util.UUID;

@Builder
public record TicketTemplateRequest(
        @JsonProperty("eventTrackingId")
        UUID eventTrackingId,

        @JsonProperty("type")
        TypeTicket type,

        @DecimalMin(value = "0.0")
        @JsonProperty("price")
        double price,

        @Min(value = 1)
        @JsonProperty("nombreTicketDisponible")
        int nombreTicketDisponible,

        @JsonProperty("creatorId")
        UUID creatorId
){
}