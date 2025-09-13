package nitchcorp.backend.titan.coursier.Application.mappers;

import lombok.AllArgsConstructor;
import nitchcorp.backend.titan.coursier.Application.dtos.requests.DeliveryPersonRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.DeliveryPersonResponse;
import nitchcorp.backend.titan.coursier.Domain.models.DeliveryCompany;
import nitchcorp.backend.titan.coursier.Domain.models.DeliveryPerson;
import nitchcorp.backend.titan.coursier.Infrastructure.DeliveryCompanyRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class DeliveryPersonMapper {

    private DeliveryCompanyRepository companyRepository;

    public DeliveryPerson toEntity(DeliveryPersonRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La demande de livreur ne peut pas être null");
        }

        DeliveryPerson person = new DeliveryPerson();
        person.setTrackingId(UUID.randomUUID());
        person.setFirstName(request.firstName());
        person.setLastName(request.lastName());
        person.setPhone(request.phone());
        person.setEmail(request.email());
        person.setVehicleType(request.vehicleType());
        person.setLicenseNumber(request.licenseNumber());

        DeliveryCompany company = companyRepository.findByTrackingId(request.deliveryCompanyTrackingId())
                .orElseThrow(() -> new IllegalArgumentException("Compagnie de livraison non trouvée"));

        if (!company.getIsActive()) {
            throw new IllegalArgumentException("La compagnie de livraison n'est pas active");
        }

        person.setDeliveryCompany(company);

        return person;
    }


    public DeliveryPersonResponse toResponse(DeliveryPerson person) {
        if (person == null) {
            throw new IllegalArgumentException("Le livreur ne peut pas être null");
        }

        return new DeliveryPersonResponse(
                person.getTrackingId(),
                person.getFirstName(),
                person.getLastName(),
                person.getPhone(),
                person.getEmail(),
                person.getVehicleType(),
                person.getLicenseNumber(),
                person.getIsAvailable(),
                person.getIsActive(),
                person.getDeliveryCompany() != null ? person.getDeliveryCompany().getTrackingId() : null
        );
    }


}
