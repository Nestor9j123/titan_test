package nitchcorp.backend.titan.food.Application.mappers;

import lombok.AllArgsConstructor;
import nitchcorp.backend.titan.food.Application.dtos.request.RestaurantLocationRequest;
import nitchcorp.backend.titan.food.Application.dtos.request.RestaurantRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.RestaurantLocationResponse;
import nitchcorp.backend.titan.food.Application.dtos.response.RestaurantResponse;
import nitchcorp.backend.titan.food.Domain.models.Restaurant;
import nitchcorp.backend.titan.food.Infrastructure.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class RestaurantMapper {

    private final RestaurantRepository restaurantRepository;

    public Restaurant toEntity(RestaurantRequest request) {
        if(request == null){
            throw new IllegalArgumentException(" la requete d'inscription du restaurant est vide ");
        }
        Restaurant restaurants = new Restaurant();

        restaurants.setTrackingId(UUID.randomUUID());
        restaurants.setName(request.name());
        restaurants.setDescription(request.description());
        restaurants.setAddress(request.address());
        restaurants.setPhone(request.phone());
        restaurants.setKitchenType(request.kitchenType());
        restaurants.setLatitude(request.latitude());
        restaurants.setLongitude(request.longitude());
        restaurants.setOpeningHour(request.openingHour());
        restaurants.setHaveDelevery(request.haveDelevery());
        restaurants.setImageUrl(request.imageUrl());
        return  restaurants;
    }

    public RestaurantResponse toResponse(Restaurant restaurant) {
        if(restaurant == null){
            throw new IllegalArgumentException("aucun restaurant n'a ete seleectioné ");
        }

        return  new RestaurantResponse(
                restaurant.getTrackingId(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getPhone(),
                restaurant.getAddress(),
                restaurant.getKitchenType(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getOpeningHour(),
                restaurant.isHaveDelevery(),
                restaurant.getImageUrl()
        );
    }

    public static  Restaurant toEntityFromResponse(RestaurantResponse response){
        if (response == null) {
            return null;
        }
        Restaurant restaurant = new Restaurant();
        restaurant.setTrackingId(response.trackingId());
        restaurant.setName(response.name());
        restaurant.setDescription(response.description());
        restaurant.setPhone(response.phone());
        restaurant.setAddress(response.address());
        restaurant.setKitchenType(response.kitchenType());
        restaurant.setLatitude(response.latitude());
        restaurant.setLongitude(response.longitude());
        restaurant.setOpeningHour(response.openingHour());
        restaurant.setHaveDelevery(response.haveDelevery());
        return restaurant;
    }

    public void updateEntityFromRequest(Restaurant entityToUpdate, RestaurantRequest request) {
        if (request == null) {
            return;
        }

        if (request.name() != null) {
            entityToUpdate.setName(request.name());
        }
        if (request.description() != null) {
            entityToUpdate.setDescription(request.description());
        }
        if (request.phone() != null) {
            entityToUpdate.setPhone(request.phone());
        }
        if (request.address() != null) {
            entityToUpdate.setAddress(request.address());
        }
        if (request.kitchenType() != null) {
            entityToUpdate.setKitchenType(request.kitchenType());
        }
        entityToUpdate.setLatitude(request.latitude());

        entityToUpdate.setLongitude(request.longitude());

        if (request.openingHour() != null) {
            entityToUpdate.setOpeningHour(request.openingHour());
        }
    }

    public void updateEntityFromLocationRequest(Restaurant entityToUpdate, RestaurantLocationRequest request) {
        if (request == null) {
            return;
        }
        if (request.latitude() != null) {
            entityToUpdate.setLatitude(request.latitude());

            if (request.longitude() != null) {
                entityToUpdate.setLongitude(request.longitude());
            }
        }
    }

    public RestaurantLocationResponse toLocationResponse(Restaurant restaurant) {
        return new RestaurantLocationResponse(
                restaurant.getTrackingId(),
                restaurant.getLatitude(),
                restaurant.getLongitude()
        );
    }


}
