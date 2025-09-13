package nitchcorp.backend.titan.events.Application.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.modulith.NamedInterface;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NamedInterface
@Builder
public record EventsRequest(
        @JsonProperty("name")
        String name,

        @JsonProperty("description")
        String description,

        @Min(value = 30)
        @JsonProperty("capacity")
        int capacity,

        @JsonProperty("startDateTime")
        LocalDateTime startDateTime,

        @JsonProperty("endDateTime")
        LocalDateTime endDateTime,

        @JsonProperty("images")
        List<String> images,

        @NotNull
        @JsonProperty("organizerId")
        UUID organizerId
) {

}