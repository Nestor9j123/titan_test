package nitchcorp.backend.titan.food.Application.dtos.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RestaurantLocationRequest(

        UUID restaurantTrackingId,

        Double latitude,

        Double longitude
) {
}
