package nitchcorp.backend.titan.immo.application.dto.responses;

import nitchcorp.backend.titan.immo.domain.enums.PropertyStatus;
import nitchcorp.backend.titan.immo.domain.enums.PropertyType;
// import nitchcorp.backend.titan.shared.config.minio.dto.FileMetadata;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PropertyResponse(
        UUID trackingId,
        Long id,
        Long ownerId,
        Long agentId,
        PropertyType type,
        String address,
        String city,
        String country,
        Double latitude,
        Double longitude,
        String description,
        BigDecimal rentPrice,
        BigDecimal additionalFees,
        BigDecimal deposit,
        Integer numberOfRooms,
        Double area,
        List<String> amenities,
        List<String> photos, // Temporarily simplified
        PropertyStatus status,
        LocalDate availabilityDate
) {}