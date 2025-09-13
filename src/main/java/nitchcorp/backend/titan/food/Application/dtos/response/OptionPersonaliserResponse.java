package nitchcorp.backend.titan.food.Application.dtos.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OptionPersonaliserResponse(

       UUID trackingId,

        UUID tracking_platId,

        String name,

        String description,

        double prix

) { }
