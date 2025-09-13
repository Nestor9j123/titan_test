package nitchcorp.backend.titan.immo.application.service;

import nitchcorp.backend.titan.immo.application.dto.requests.PropertyRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.PropertyResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PropertyService {
    PropertyResponse createProperty(PropertyRequest request);
    List<PropertyResponse> getAllProperties();
    PropertyResponse updateProperty(UUID trackingId, PropertyRequest request);
    void deleteProperty(UUID trackingId);
    PropertyResponse getPropertyByTrackingId(UUID trackingId);
        List<PropertyResponse> searchProperties(String city, String country, Double priceMin, Double priceMax, String type, String availabilityDate);

    List<PropertyResponse> searchPropertiesNearby(Double latitude, Double longitude, Double radiusKm, String country);

    List<PropertyResponse> getPropertiesByCountry(String country);

    List<PropertyResponse> getPropertiesByAgent(UUID agentId);

    List<PropertyResponse> getPropertiesByOwner(UUID ownerId);

    PropertyResponse updatePropertyStatus(UUID trackingId, String status);

    PropertyResponse createPropertyWithPhotos(PropertyRequest request, List<MultipartFile> photos);

    PropertyResponse addPhotosToProperty(UUID trackingId, List<MultipartFile> photos);

    PropertyResponse removePhotosFromProperty(UUID trackingId, List<Long> imageIds);

    List<PropertyResponse> getAvailableProperties(String country);

    Page<PropertyResponse> getPropertyByAdresse(int page, int size, String sortBy, String sortDir, String adresse);
    Page<PropertyResponse> getPropertyByCountry(int page, int size, String sortBy, String sortDir, String country);
    Page<PropertyResponse> getPropertyByCity(int page, int size, String sortBy , String sortDir, String city);
}