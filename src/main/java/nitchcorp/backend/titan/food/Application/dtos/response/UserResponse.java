package nitchcorp.backend.titan.food.Application.dtos.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserResponse(

        UUID trackingId,

        String name,

        String surname,

        String email,

        String password,

        LocalDateTime dateInscription

) { }
