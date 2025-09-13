package nitchcorp.backend.titan.immo.application.mapper;

import lombok.RequiredArgsConstructor;
import nitchcorp.backend.titan.immo.application.dto.requests.PropertyRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.PropertyResponse;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.immo.domain.model.Property;
import nitchcorp.backend.titan.shared.minio.service.MinioService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PropertyMapper {

    private final MinioService minioService;

    public PropertyResponse toResponse(Property property) {
        if (property == null) {
            throw new IllegalArgumentException("Property cannot be null");
        }

        return new PropertyResponse(
                property.getTrackingId(),
                property.getId(),
                (property.getOwner() != null) ? property.getOwner().getId() : null,
                (property.getAgent() != null) ? property.getAgent().getId() : null,
                property.getType(),
                property.getAddress(),
                property.getCity(),
                property.getCountry(),
                property.getLatitude(),
                property.getLongitude(),
                property.getDescription(),
                property.getRentPrice(),
                property.getAdditionalFees(),
                property.getDeposit(),
                property.getNumberOfRooms(),
                property.getArea(),
                property.getAmenities(),
                property.getPhotos() != null ? property.getPhotos() : List.of(),
                property.getStatus(),
                property.getAvailabilityDate()
        );
    }

    public Property toEntity(PropertyRequest request, User owner, User agent) {
        if (request == null) {
            throw new IllegalArgumentException("PropertyRequest cannot be null");
        }

        Property property = new Property();
        property.setTrackingId(UUID.randomUUID());
        property.setOwner(owner);
        property.setAgent(agent);
        property.setType(request.type());
        property.setAddress(request.address());
        property.setCity(request.city());
        property.setCountry(request.country());
        property.setLatitude(request.latitude());
        property.setLongitude(request.longitude());
        property.setDescription(request.description());
        property.setRentPrice(request.rentPrice());
        property.setAdditionalFees(request.additionalFees());
        property.setDeposit(request.deposit());
        property.setNumberOfRooms(request.numberOfRooms());
        property.setArea(request.area());
        property.setAmenities(request.amenities());
        property.setPhotos(request.photos());
        property.setStatus(request.status());
        property.setAvailabilityDate(request.availabilityDate());

        return property;
    }

    public static Property toEntityFromResponse(PropertyResponse response, User owner, User agent) {
        if (response == null) {

            throw new IllegalArgumentException("PropertyResponse cannot be null");
        }

        Property property = new Property();
        property.setTrackingId(response.trackingId());
        property.setId(response.id());
        property.setOwner(owner);
        property.setAgent(agent);
        property.setType(response.type());
        property.setAddress(response.address());
        property.setCity(response.city());
        property.setCountry(response.country());
        property.setLatitude(response.latitude());
        property.setLongitude(response.longitude());
        property.setDescription(response.description());
        property.setRentPrice(response.rentPrice());
        property.setAdditionalFees(response.additionalFees());
        property.setDeposit(response.deposit());
        property.setNumberOfRooms(response.numberOfRooms());
        property.setArea(response.area());
        property.setAmenities(response.amenities());
        property.setPhotos(response.photos());
        property.setStatus(response.status());
        property.setAvailabilityDate(response.availabilityDate());

        return property;
    }
}