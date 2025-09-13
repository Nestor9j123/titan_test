package nitchcorp.backend.titan.coursier.Application.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record AdressDTO(
        @NotBlank(message = "L'adresse est obligatoire")
        String streetAddress,

        String city,
        String postalCode,

        @NotBlank(message = "Le pays est obligatoire")
        String country,

        @NotBlank(message = "Les informations additionnelles sont obligatoires")
        String additionalInfo,

        Double latitude,
        Double longitude
) {
        @JsonCreator
        public AdressDTO(
                @JsonProperty("streetAddress") String streetAddress,
                @JsonProperty("city") String city,
                @JsonProperty("postalCode") String postalCode,
                @JsonProperty("country") String country,
                @JsonProperty("additionalInfo") String additionalInfo,
                @JsonProperty("latitude") Double latitude,
                @JsonProperty("longitude") Double longitude) {
                this.streetAddress = streetAddress;
                this.city = city;
                this.postalCode = postalCode;
                this.country = country;
                this.additionalInfo = additionalInfo;
                this.latitude = latitude;
                this.longitude = longitude;
        }
}
