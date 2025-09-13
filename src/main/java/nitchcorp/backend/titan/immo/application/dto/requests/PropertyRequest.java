package nitchcorp.backend.titan.immo.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nitchcorp.backend.titan.immo.domain.enums.PropertyStatus;
import nitchcorp.backend.titan.immo.domain.enums.PropertyType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PropertyRequest(
        @JsonProperty("ownerId")
        @NotNull
        UUID ownerId,

        @JsonProperty("agentId")
        UUID agentId,

        @JsonProperty("type")
        @NotNull
        PropertyType type,

        @JsonProperty("address")
        @NotNull(message = "L'adresse est requise")
        @NotBlank(message = "L'adresse ne peut pas être vide")
        String address,

        @JsonProperty("city")
        @NotNull(message = "La ville est requise")
        @NotBlank(message = "La ville ne peut pas être vide")
        String city,

        @JsonProperty("country")
        @NotNull(message = "Le pays est requis")
        @NotBlank(message = "Le pays ne peut pas être vide")
        String country,

        @JsonProperty("latitude")
        Double latitude,

        @JsonProperty("longitude")
        Double longitude,

        @JsonProperty("description")
        @NotNull
        String description,

        @JsonProperty("rentPrice")
        @NotNull
        BigDecimal rentPrice,

        @JsonProperty("additionalFees")
        BigDecimal additionalFees,

        @JsonProperty("deposit")
        BigDecimal deposit,

        @JsonProperty("numberOfRooms")
        Integer numberOfRooms,

        @JsonProperty("area")
        Double area,

        @JsonProperty("amenities")
        List<String> amenities,

        @JsonProperty("photos")
        List<String> photos,

        @JsonProperty("status")
        @NotNull
        PropertyStatus status,

        @JsonProperty("availabilityDate")
        LocalDate availabilityDate
) {}
