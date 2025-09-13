package nitchcorp.backend.titan.coursier.Application.dtos.responses;

import java.util.UUID;

public record DeliveryCompanyResponse(

        UUID trackingId,

        String name,

        String description,

        String contactEmail,

        String contactPhone,

        String address,

        Boolean isActive
) {
}
