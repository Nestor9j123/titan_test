package nitchcorp.backend.titan.coursier.Application.mappers;

import lombok.AllArgsConstructor;
import nitchcorp.backend.titan.coursier.Application.dtos.requests.DeliveryCompanyRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.DeliveryCompanyResponse;
import nitchcorp.backend.titan.coursier.Domain.models.DeliveryCompany;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class DeliveryCompanyMapper {

    public DeliveryCompany toEntity(DeliveryCompanyRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La compagnie n'existe pas");
        }
        DeliveryCompany deliveryCompany = new DeliveryCompany();

        deliveryCompany.setTrackingId(UUID.randomUUID());
        deliveryCompany.setName(request.name());
        deliveryCompany.setDescription(request.description());
        deliveryCompany.setContactEmail(request.contactEmail());
        deliveryCompany.setContactPhone(request.contactPhone());
        deliveryCompany.setAddress(request.address());

        return deliveryCompany;
    }

    public DeliveryCompanyResponse toResponse(DeliveryCompany deliveryCompany) {
        if (deliveryCompany == null) {
            throw new IllegalArgumentException("La compagnie est null");
        }

        return new DeliveryCompanyResponse(
                deliveryCompany.getTrackingId(),
                deliveryCompany.getName(),
                deliveryCompany.getDescription(),
                deliveryCompany.getContactEmail(),
                deliveryCompany.getContactPhone(),
                deliveryCompany.getAddress(),
                deliveryCompany.getIsActive()
        );
    }
}
