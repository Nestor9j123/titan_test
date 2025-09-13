package nitchcorp.backend.titan.coursier.Application.dtos.responses;

import nitchcorp.backend.titan.coursier.Domain.enums.TypeVehicle;

import java.util.UUID;

public record DeliveryPersonResponse(

         UUID trackingId,

         String firstName,

         String lastName,

         String phone,

         String email,

         TypeVehicle vehicleType,

         String licenseNumber,

         Boolean isAvailable,

         Boolean isActive,

         UUID deliveryCompanyTrackingId
) {
}
