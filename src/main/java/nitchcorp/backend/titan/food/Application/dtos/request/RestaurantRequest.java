package nitchcorp.backend.titan.food.Application.dtos.request;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RestaurantRequest (

         String name,

         String phone,

         String description,

         String address,

         String kitchenType,

         Double latitude,

         Double longitude,

         LocalDateTime openingHour,

         boolean haveDelevery,

         List<String> imageUrl
) {
}
