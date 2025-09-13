package nitchcorp.backend.titan.food.Application.dtos.request;

import lombok.Builder;

@Builder
public record NotePlatRequest(
        int note
) {
}
