package nitchcorp.backend.titan.food.Application.dtos.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RestaurantLocationResponse(

        UUID restaurantTrackingId,

        Double latitude,

        Double longitude
) {
}
