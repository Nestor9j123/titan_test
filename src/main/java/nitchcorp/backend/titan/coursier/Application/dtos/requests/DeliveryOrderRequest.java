package nitchcorp.backend.titan.coursier.Application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nitchcorp.backend.titan.coursier.Application.dtos.AdressDTO;
import nitchcorp.backend.titan.coursier.Application.dtos.ContactInfoDTO;

import java.util.UUID;

public record DeliveryOrderRequest(

        @NotBlank(message = "Le titre est obligatoire")
        String title,

        String details,

        @Valid
        @NotNull(message = "L'adresse de récupération est obligatoire")
        AdressDTO pickupAddress,

        @Valid
        @NotNull(message = "Le contact de récupération est obligatoire")
        ContactInfoDTO pickupContact,

        @Valid
        @NotNull(message = "L'adresse de destination est obligatoire")
        AdressDTO deliveryAddress,

        @Valid
        @NotNull(message = "Le contact de destination est obligatoire")
        ContactInfoDTO deliveryContact,

        @NotNull(message = "L'ID de la compagnie de livraison est obligatoire")
        UUID deliveryCompanyTrackingId,

        @NotNull(message = "L'ID de la personne de livraison est obligatoire")
        UUID assignedDeliveryPersonTrackingId
) {
        @JsonCreator
        public DeliveryOrderRequest(
                @JsonProperty("title") String title,
                @JsonProperty("details") String details,
                @JsonProperty("pickupAddress") AdressDTO pickupAddress,
                @JsonProperty("pickupContact") ContactInfoDTO pickupContact,
                @JsonProperty("deliveryAddress") AdressDTO deliveryAddress,
                @JsonProperty("deliveryContact") ContactInfoDTO deliveryContact,
                @JsonProperty("deliveryCompanyTrackingId") UUID deliveryCompanyTrackingId,
                @JsonProperty("assignedDeliveryPersonTrackingId") UUID assignedDeliveryPersonTrackingId) {
                this.title = title;
                this.details = details;
                this.pickupAddress = pickupAddress;
                this.pickupContact = pickupContact;
                this.deliveryAddress = deliveryAddress;
                this.deliveryContact = deliveryContact;
                this.deliveryCompanyTrackingId = deliveryCompanyTrackingId;
                this.assignedDeliveryPersonTrackingId = assignedDeliveryPersonTrackingId;
        }
}
