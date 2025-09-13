package nitchcorp.backend.titan.food.Application.dtos.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record RestaurantResponse(

        UUID trackingId,

        String name,

        String phone,

        String description,

        String address,

        String kitchenType,

        Double latitude,

        Double longitude,

        LocalDateTime openingHour,

        boolean haveDelevery,

        java.util.List<String> imageUrl
) {}
