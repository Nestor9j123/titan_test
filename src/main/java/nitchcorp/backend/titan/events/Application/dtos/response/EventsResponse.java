package nitchcorp.backend.titan.events.Application.dtos.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.modulith.NamedInterface;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NamedInterface
@Builder
public record EventsResponse(
        @JsonProperty("uuid")
        UUID trackingId,

        @JsonProperty("name")
        String name,

        @JsonProperty("description")
        String description,

        @JsonProperty("capacity")
        int capacity,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime startDateTime,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime endDateTime,

        List<String> images,

        @JsonProperty("organizerId")
        UUID organizerId,

        @JsonProperty("organizerName")
        String organizerName,

        @JsonProperty("organizerEmail")
        String organizerEmail

) {}