package nitchcorp.backend.titan.events.Application.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OptionResponse(
        @JsonProperty("tracking_id")
        UUID trackingId  ,
        @JsonProperty("name")
        String name
) {}
