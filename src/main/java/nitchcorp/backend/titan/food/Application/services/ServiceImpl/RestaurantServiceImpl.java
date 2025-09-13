package nitchcorp.backend.titan.food.Application.services.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import nitchcorp.backend.titan.food.Application.dtos.request.RestaurantLocationRequest;
import nitchcorp.backend.titan.food.Application.dtos.request.RestaurantRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.RestaurantLocationResponse;
import nitchcorp.backend.titan.food.Application.dtos.response.RestaurantResponse;
import nitchcorp.backend.titan.food.Application.mappers.RestaurantMapper;
import nitchcorp.backend.titan.food.Domain.models.Restaurant;
import nitchcorp.backend.titan.food.Infrastructure.RestaurantRepository;
import nitchcorp.backend.titan.food.Application.services.RestaurantService;
import nitchcorp.backend.titan.shared.minio.enums.FileType;
import nitchcorp.backend.titan.shared.minio.service.MinioService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RestaurantServiceImpl.class);
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantsMapper;
    private final MinioService minioService;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, RestaurantMapper restaurantsMapper, MinioService minioService) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantsMapper = restaurantsMapper;
        this.minioService = minioService;
    }

    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest restaurantRequest) {
        Restaurant restaurants = restaurantsMapper.toEntity(restaurantRequest);
        Restaurant saveRestaurants = restaurantRepository.save(restaurants);

        return restaurantsMapper.toResponse(saveRestaurants);
    }

    @Override
    public RestaurantResponse getRestaurantByTrackingId(UUID trackingId) {
        Restaurant restaurants = restaurantRepository.findByTrackingId(trackingId)
                .orElseThrow(()-> new EntityNotFoundException("Restaurant not found by " + trackingId ));

        return restaurantsMapper.toResponse(restaurants);
    }

    @Override
    public List<RestaurantResponse> getAllRestaurants() {
        return  restaurantRepository.findAllRestaurants().stream()
                .map(restaurantsMapper::toResponse).toList();
    }

    @Override
    public void deleteRestaurant(UUID trackingId) {
        Restaurant restaurants = restaurantRepository.findByTrackingId(trackingId)
                .orElseThrow(()-> new EntityNotFoundException("Restaurant not found by " + trackingId ));

        restaurantRepository.delete(restaurants);
    }

    @Override
    public RestaurantResponse updateRestaurant(UUID trackingId, RestaurantRequest restaurantRequest) {
        Restaurant existingRestaurant = restaurantRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + trackingId));
        existingRestaurant.setName(restaurantRequest.name());
        existingRestaurant.setDescription(restaurantRequest.description());
        existingRestaurant.setPhone(restaurantRequest.phone());
        existingRestaurant.setAddress(restaurantRequest.address());
        existingRestaurant.setLatitude(restaurantRequest.latitude());
        existingRestaurant.setLongitude(restaurantRequest.longitude());
        existingRestaurant.setKitchenType(restaurantRequest.kitchenType());
        existingRestaurant.setOpeningHour(restaurantRequest.openingHour());
        existingRestaurant.setHaveDelevery(restaurantRequest.haveDelevery());

        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);

        return restaurantsMapper.toResponse(updatedRestaurant);
    }

    // RestaurantServiceImpl.java

    @Override
    @Transactional
    public RestaurantResponse createRestaurantWithPhotos(RestaurantRequest request, List<MultipartFile> photos) {
        log.info("Début création restaurant avec photos: {}", request);

        // Création du restaurant sans image
        RestaurantResponse restaurant = createRestaurant(request);
        log.info("Restaurant créé avec trackingId: {}", restaurant.trackingId());

        if (photos != null && !photos.isEmpty()) {
            log.info("Traitement de {} photo(s) pour le restaurant {}", photos.size(), restaurant.trackingId());

            Restaurant savedRestaurant = restaurantRepository.findByTrackingId(restaurant.trackingId())
                    .orElseThrow(() -> new RuntimeException("Restaurant non trouvé avec trackingId: " + restaurant.trackingId()));

            try {
                // Upload des images
                var uploadResponses = minioService.uploadMultipleFiles(photos, FileType.IMAGE);
                List<String> imageUrls = uploadResponses.stream()
                        .map(response -> response.getFileUrl())
                        .collect(Collectors.toList());

                // Ajout des URLs dans l'entité
                savedRestaurant.setImageUrl(imageUrls);
                Restaurant updatedRestaurant = restaurantRepository.save(savedRestaurant);

                return restaurantsMapper.toResponse(updatedRestaurant);
            } catch (Exception e) {
                log.error("Erreur upload images: {}", e.getMessage(), e);
                throw new RuntimeException("Échec upload images: " + e.getMessage(), e);
            }
        } else {
            log.info("Aucune image fournie pour le restaurant {}", restaurant.trackingId());
        }

        return restaurant;
    }

    @Override
    public RestaurantLocationResponse updateRestaurantLocation(UUID trackingId, RestaurantLocationRequest restaurantLocationRequest) {
        Restaurant existingRestaurant = restaurantRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant non trouvé avec trackingId: " + trackingId));

        restaurantsMapper.updateEntityFromLocationRequest(existingRestaurant, restaurantLocationRequest);

        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);

        return restaurantsMapper.toLocationResponse(updatedRestaurant);
    }

}
