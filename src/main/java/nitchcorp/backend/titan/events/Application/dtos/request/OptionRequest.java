package nitchcorp.backend.titan.events.Application.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record OptionRequest(
        @JsonProperty("name")
        String name
) {}
