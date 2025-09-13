package nitchcorp.backend.titan.coursier.Application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nitchcorp.backend.titan.coursier.Domain.enums.TypeVehicle;

import java.util.UUID;

public record DeliveryPersonRequest(

        String firstName,

        String lastName,

        String phone,

        String email,

        TypeVehicle vehicleType,

        String licenseNumber,

        UUID deliveryCompanyTrackingId

) {
    @JsonCreator
    public DeliveryPersonRequest(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("phone") String phone,
            @JsonProperty("email") String email,
            @JsonProperty("vehicleType") TypeVehicle vehicleType,
            @JsonProperty("licenseNumber") String licenseNumber,
            @JsonProperty("deliveryCompanyTrackingId") UUID deliveryCompanyTrackingId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.vehicleType = vehicleType;
        this.licenseNumber = licenseNumber;
        this.deliveryCompanyTrackingId = deliveryCompanyTrackingId;
    }
}
