package nitchcorp.backend.titan.food.Application.services;

import nitchcorp.backend.titan.food.Application.dtos.request.RestaurantLocationRequest;
import nitchcorp.backend.titan.food.Application.dtos.request.RestaurantRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.RestaurantLocationResponse;
import nitchcorp.backend.titan.food.Application.dtos.response.RestaurantResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface RestaurantService {

    RestaurantResponse createRestaurant(RestaurantRequest restaurantRequest);

    RestaurantResponse getRestaurantByTrackingId(UUID trackingId);

    List<RestaurantResponse> getAllRestaurants();

    void deleteRestaurant(UUID trackingId);

    RestaurantResponse updateRestaurant(UUID trackingId, RestaurantRequest restaurantRequest);

    RestaurantResponse createRestaurantWithPhotos(RestaurantRequest request, List<MultipartFile> photos);

    RestaurantLocationResponse updateRestaurantLocation(UUID trackingId, RestaurantLocationRequest restaurantLocationRequest);

}
