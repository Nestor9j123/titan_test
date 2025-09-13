package nitchcorp.backend.titan.food.Application.dtos.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OptionPersonaliserRequest(

        UUID tracking_platId,

        String name,

        String description,

        double prix

) { }
