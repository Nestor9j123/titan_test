package nitchcorp.backend.titan.coursier.Application.dtos.responses;

import nitchcorp.backend.titan.coursier.Application.dtos.AdressDTO;
import nitchcorp.backend.titan.coursier.Application.dtos.ContactInfoDTO;
import nitchcorp.backend.titan.coursier.Domain.enums.DeliveryStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryOrderResponse(
        UUID trackingId,

        String title,

        String details,

        AdressDTO pickupAddress,

        ContactInfoDTO pickupContact,

        AdressDTO deliveryAddress,

        ContactInfoDTO deliveryContact,

        Double distanceInKm,

        BigDecimal calculatedPrice,

        DeliveryStatus status,

        LocalDateTime createdAt,

        LocalDateTime assignedAt,

        LocalDateTime pickedUpAt,

        LocalDateTime deliveredAt,

        UUID deliveryCompanyTrackingId,

        UUID assignedDeliveryPersonTrackingId
) {

}
