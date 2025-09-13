package nitchcorp.backend.titan.events.Application.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PurchaseTicketRequest(

        @JsonProperty("buyerId")
        UUID buyerId,

        @JsonProperty("templateTrackingId")
        UUID templateTrackingId,

        @JsonProperty("nombreTicketAchete")
        @Min(value = 1)
        int nombreTicketAchete
) {}