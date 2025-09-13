package nitchcorp.backend.titan.events.Application.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record VotesRequest(
        @JsonProperty("question")
        String question,

        @JsonProperty("options")
        @Size(min = 2)
        List<OptionRequest> options,

        @JsonProperty("eventId")
        UUID eventId,

        @JsonProperty("creatorId")
        UUID creatorId
) {
}