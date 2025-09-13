package nitchcorp.backend.titan.coursier.Application.dtos.requests;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nitchcorp.backend.titan.coursier.Application.dtos.AdressDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record PriceCalculationRequest(
        @NotNull
        @Valid
        AdressDTO pickupAddress,

        @NotNull
        @Valid
        AdressDTO deliveryAddress,

        @NotNull
        Long deliveryCompanyId
) {
        @JsonCreator
        public PriceCalculationRequest(
                @JsonProperty("pickupAddress") AdressDTO pickupAddress,
                @JsonProperty("deliveryAddress") AdressDTO deliveryAddress,
                @JsonProperty("deliveryCompanyId") Long deliveryCompanyId) {
                this.pickupAddress = pickupAddress;
                this.deliveryAddress = deliveryAddress;
                this.deliveryCompanyId = deliveryCompanyId;
        }
}
