package nitchcorp.backend.titan.coursier.Application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record DeliveryCompanyRequest(
        @NotNull(message = "Le nom de la compagnie de livraison est obligatoire")
        String name,

        @NotNull(message = "La description de la compagnie de livraison est obligatoire")
        String description,

        @NotNull(message = "Le contact de la compagnie de livraison est obligatoire")
        String contactEmail,

        String contactPhone,

        String address
) {
    @JsonCreator
    public DeliveryCompanyRequest(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("contactEmail") String contactEmail,
            @JsonProperty("contactPhone") String contactPhone,
            @JsonProperty("address") String address) {
        this.name = name;
        this.description = description;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.address = address;
    }

}
