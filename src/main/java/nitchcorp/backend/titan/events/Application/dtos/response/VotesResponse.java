package nitchcorp.backend.titan.events.Application.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import nitchcorp.backend.titan.events.domain.model.Options;

import java.util.List;
import java.util.UUID;

@Builder
public record VotesResponse(
        @JsonProperty("trackingId")
        UUID trackingId,

        @JsonProperty("question")
        String question,

        List<Options> options,

        @JsonProperty("statusVote")
        boolean statusVote,

        @JsonProperty("eventTrackingId")
        UUID eventTrackingId,

        @JsonProperty("creatorId")
        UUID creatorId
) {}