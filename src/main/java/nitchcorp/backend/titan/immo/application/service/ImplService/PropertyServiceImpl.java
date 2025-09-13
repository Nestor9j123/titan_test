package nitchcorp.backend.titan.immo.application.service.ImplService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.immo.application.dto.requests.PropertyRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.PropertyResponse;
import nitchcorp.backend.titan.immo.application.mapper.PropertyMapper;
import nitchcorp.backend.titan.immo.domain.model.Agent;
import nitchcorp.backend.titan.immo.domain.model.Owner;
import nitchcorp.backend.titan.immo.domain.model.Property;
import nitchcorp.backend.titan.immo.domain.enums.PropertyStatus;
import nitchcorp.backend.titan.immo.domain.exceptions.PropertyNotFoundException;
import nitchcorp.backend.titan.immo.infrastructure.AgentRepository;
import nitchcorp.backend.titan.immo.infrastructure.OwnerRepository;
import nitchcorp.backend.titan.immo.infrastructure.PropertyRepository;
import nitchcorp.backend.titan.immo.application.service.PropertyService;
import nitchcorp.backend.titan.shared.minio.service.MinioService;
import nitchcorp.backend.titan.shared.minio.enums.FileType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final OwnerRepository ownerRepository;
    private final AgentRepository agentRepository;
    private final PropertyMapper propertyMapper;
    private final MinioService minioService;

    @Override
    public PropertyResponse createProperty(PropertyRequest request) {
        Owner owner = ownerRepository.getOwnerByTrackingId(request.ownerId())
                .orElseThrow(() -> new PropertyNotFoundException("Owner not found with ID: " + request.ownerId()));
        Agent agent = request.agentId() != null ? agentRepository.getAgentByTrackingId(request.agentId())
                .orElseThrow(() -> new PropertyNotFoundException("Agent not found with ID: " + request.agentId())) : null;

        Property property = propertyMapper.toEntity(request, owner, agent);
        Property savedProperty = propertyRepository.save(property);

        return propertyMapper.toResponse(savedProperty);
    }

    @Override
    public List<PropertyResponse> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();
        return properties.stream().map(propertyMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PropertyResponse updateProperty(UUID trackingId, PropertyRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("PropertyRequest cannot be null");
        }
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        Property existingProperty = propertyRepository.getPropertiesByTrackingId(trackingId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with trackingId: " + trackingId));

        Owner owner = ownerRepository.getOwnerByTrackingId(request.ownerId())
                .orElseThrow(() -> new PropertyNotFoundException("Owner not found with ID: " + request.ownerId()));
        Agent agent = request.agentId() != null ? agentRepository.getAgentByTrackingId(request.agentId())
                .orElseThrow(() -> new PropertyNotFoundException("Agent not found with ID: " + request.agentId())) : null;

        existingProperty.setOwner(owner);
        existingProperty.setAgent(agent);
        existingProperty.setType(request.type());
        existingProperty.setAddress(request.address());
        existingProperty.setCity(request.city());
        existingProperty.setCountry(request.country());
        existingProperty.setLatitude(request.latitude());
        existingProperty.setLongitude(request.longitude());
        existingProperty.setDescription(request.description());
        existingProperty.setRentPrice(request.rentPrice());
        existingProperty.setAdditionalFees(request.additionalFees());
        existingProperty.setDeposit(request.deposit());
        existingProperty.setNumberOfRooms(request.numberOfRooms());
        existingProperty.setArea(request.area());
        existingProperty.setAmenities(request.amenities());
        existingProperty.setStatus(request.status());
        existingProperty.setAvailabilityDate(request.availabilityDate());

        Property updatedProperty = propertyRepository.save(existingProperty);
        return propertyMapper.toResponse(updatedProperty);
    }

    @Override
    @Transactional
    public void deleteProperty(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        Property existingProperty = propertyRepository.getPropertiesByTrackingId(trackingId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with trackingId: " + trackingId));

        // TODO: Implement deletion of property images using MinioService
        // minioService.deleteFile(fileName, FileType.IMAGE);
        propertyRepository.delete(existingProperty);
    }

    @Override
    public PropertyResponse getPropertyByTrackingId(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        Property property = propertyRepository.getPropertiesByTrackingId(trackingId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with trackingId: " + trackingId));

        return propertyMapper.toResponse(property);
    }

    @Override
    public List<PropertyResponse> searchProperties(String city, String country, Double priceMin, Double priceMax, String type, String availabilityDate) {
        if (priceMin != null && priceMax != null && priceMin > priceMax) {
            throw new IllegalArgumentException("priceMin must be less than or equal to priceMax");
        }

        LocalDate availabilityDateParsed = null;
        if (availabilityDate != null && !availabilityDate.isEmpty()) {
            try {
                availabilityDateParsed = LocalDate.parse(availabilityDate);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid availabilityDate format. Use YYYY-MM-DD");
            }
        }

        List<Property> properties = propertyRepository.findAll();
        final LocalDate finalAvailabilityDate = availabilityDateParsed;
        final Double finalPriceMin = priceMin;
        final Double finalPriceMax = priceMax;

        List<Property> filteredProperties = properties.stream()
                .filter(p -> city == null || p.getCity().equalsIgnoreCase(city))
                .filter(p -> country == null || p.getCountry().equalsIgnoreCase(country))
                .filter(p -> finalPriceMin == null || p.getRentPrice().compareTo(BigDecimal.valueOf(finalPriceMin)) >= 0)
                .filter(p -> finalPriceMax == null || p.getRentPrice().compareTo(BigDecimal.valueOf(finalPriceMax)) <= 0)
                .filter(p -> type == null || p.getType().name().equalsIgnoreCase(type))
                .filter(p -> finalAvailabilityDate == null || p.getAvailabilityDate() != null && !p.getAvailabilityDate().isBefore(finalAvailabilityDate))
                .collect(Collectors.toList());

        return filteredProperties.stream()
                .map(propertyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyResponse> searchPropertiesNearby(Double latitude, Double longitude, Double radiusKm, String country) {
        List<Property> properties = country != null ? propertyRepository.findByCountry(country) : propertyRepository.findAll();

        return properties.stream()
                .filter(p -> p.getLatitude() != null && p.getLongitude() != null)
                .filter(p -> calculateDistance(latitude, longitude, p.getLatitude(), p.getLongitude()) <= radiusKm)
                .map(propertyMapper::toResponse)
                .collect(Collectors.toList());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public List<PropertyResponse> getPropertiesByCountry(String country) {
        return propertyRepository.findByCountry(country).stream()
                .map(propertyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyResponse> getPropertiesByAgent(UUID agentId) {
        return propertyRepository.findByAgentTrackingId(agentId).stream()
                .map(propertyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyResponse> getPropertiesByOwner(UUID ownerId) {
        return propertyRepository.findByOwnerTrackingId(ownerId).stream()
                .map(propertyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PropertyResponse updatePropertyStatus(UUID trackingId, String status) {
        Property property = propertyRepository.getPropertiesByTrackingId(trackingId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with trackingId: " + trackingId));
        try {
            PropertyStatus newStatus = PropertyStatus.valueOf(status.toUpperCase());
            property.setStatus(newStatus);
            Property updatedProperty = propertyRepository.save(property);
            return propertyMapper.toResponse(updatedProperty);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }

    @Override
    @Transactional
    public PropertyResponse createPropertyWithPhotos(PropertyRequest request, List<MultipartFile> photos) {
        log.info("Starting createPropertyWithPhotos with request: {}", request);
        
        try {
            // First create the property
            PropertyResponse property = createProperty(request);
            log.info("Property created with ID: {}", property.trackingId());
            
            // If photos are provided, upload them and add URLs to property
            if (photos != null && !photos.isEmpty()) {
                log.info("Processing {} photos for property {}", photos.size(), property.trackingId());
                
                Property savedProperty = propertyRepository.getPropertiesByTrackingId(property.trackingId())
                        .orElseThrow(() -> new PropertyNotFoundException("Property not found with trackingId: " + property.trackingId()));
                
                try {
                    // Upload photos and get URLs
                    log.debug("Uploading {} files to MinIO", photos.size());
                    var uploadResponses = minioService.uploadMultipleFiles(photos, FileType.IMAGE);
                    log.debug("MinIO upload completed. Responses: {}", uploadResponses);
                    
                    List<String> photoUrls = uploadResponses.stream()
                            .peek(response -> log.debug("Uploaded file: {}", response.getFileUrl()))
                            .map(response -> response.getFileUrl())
                            .collect(Collectors.toList());
                    
                    log.info("Successfully uploaded {} photos for property {}", photoUrls.size(), property.trackingId());
                    
                    // Add photo URLs to property
                    savedProperty.setPhotos(photoUrls);
                    Property updatedProperty = propertyRepository.save(savedProperty);
                    log.info("Updated property with {} photo URLs", photoUrls.size());
                    
                    return propertyMapper.toResponse(updatedProperty);
                } catch (Exception e) {
                    log.error("Error uploading photos to MinIO: {}", e.getMessage(), e);
                    throw new RuntimeException("Failed to upload photos: " + e.getMessage(), e);
                }
            } else {
                log.info("No photos provided for property {}", property.trackingId());
            }
            
            return property;
        } catch (Exception e) {
            log.error("Error in createPropertyWithPhotos: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public PropertyResponse addPhotosToProperty(UUID trackingId, List<MultipartFile> photos) {
        log.info("Adding photos to property: {}", trackingId);
        
        try {
            Property property = propertyRepository.getPropertiesByTrackingId(trackingId)
                    .orElseThrow(() -> new PropertyNotFoundException("Property not found with trackingId: " + trackingId));

            if (photos == null || photos.isEmpty()) {
                log.warn("No photos provided for property {}", trackingId);
                return propertyMapper.toResponse(property);
            }

            log.debug("Uploading {} photos to MinIO for property {}", photos.size(), trackingId);
            var uploadResponses = minioService.uploadMultipleFiles(photos, FileType.IMAGE);
            log.debug("MinIO upload completed with {} responses", uploadResponses.size());
            
            List<String> photoUrls = uploadResponses.stream()
                    .peek(response -> log.debug("Uploaded file URL: {}", response.getFileUrl()))
                    .map(response -> response.getFileUrl())
                    .collect(Collectors.toList());
            
            // Add new photo URLs to existing photos
            List<String> existingPhotos = property.getPhotos() != null ? property.getPhotos() : new ArrayList<>();
            List<String> allPhotos = new ArrayList<>(existingPhotos);
            allPhotos.addAll(photoUrls);
            
            log.info("Successfully added {} photos to property {}. Total photos: {}", 
                    photoUrls.size(), trackingId, allPhotos.size());
            
            property.setPhotos(allPhotos);
            Property updatedProperty = propertyRepository.save(property);
            
            return propertyMapper.toResponse(updatedProperty);
        } catch (Exception e) {
            log.error("Error adding photos to property {}: {}", trackingId, e.getMessage(), e);
            throw new RuntimeException("Failed to add photos: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public PropertyResponse removePhotosFromProperty(UUID trackingId, List<Long> imageIds) {
        Property property = propertyRepository.getPropertiesByTrackingId(trackingId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with trackingId: " + trackingId));

        // TODO: Implement deletion of specific images using MinioService
        // imageIds.forEach(imageId -> minioService.deleteFile(imageId, FileType.IMAGE));
        return propertyMapper.toResponse(property);
    }

    @Override
    public List<PropertyResponse> getAvailableProperties(String country) {
        List<Property> properties = country != null ? propertyRepository.findByCountryAndStatus(country, PropertyStatus.AVAILABLE) : propertyRepository.findByStatus(PropertyStatus.AVAILABLE);
        return properties.stream()
                .map(propertyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PropertyResponse> getPropertyByAdresse(int page, int size, String sortBy, String sortDir, String address) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return propertyRepository.findByAddress(address, pageable)
                .map(propertyMapper::toResponse);      }


    @Override
    public Page<PropertyResponse> getPropertyByCountry(int page, int size, String sortBy, String sortDir, String country) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return propertyRepository.findByCountry(country, pageable)
                .map(propertyMapper::toResponse);    }

    @Override
    public Page<PropertyResponse> getPropertyByCity(int page, int size, String sortBy, String sortDir, String city) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return propertyRepository.findByCity(city, pageable)
                .map(propertyMapper::toResponse);      }


}