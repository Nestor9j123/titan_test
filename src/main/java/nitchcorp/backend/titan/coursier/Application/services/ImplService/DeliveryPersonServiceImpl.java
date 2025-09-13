package nitchcorp.backend.titan.coursier.Application.services.ImplService;

import lombok.AllArgsConstructor;
import nitchcorp.backend.titan.coursier.Application.dtos.requests.DeliveryPersonRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.DeliveryPersonResponse;
import nitchcorp.backend.titan.coursier.Application.mappers.DeliveryPersonMapper;
import nitchcorp.backend.titan.coursier.Domain.models.DeliveryCompany;
import nitchcorp.backend.titan.coursier.Domain.models.DeliveryPerson;
import nitchcorp.backend.titan.coursier.Infrastructure.DeliveryCompanyRepository;
import nitchcorp.backend.titan.coursier.Infrastructure.DeliveryPersonRepository;
import nitchcorp.backend.titan.coursier.Application.services.DeliveryPersonService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DeliveryPersonServiceImpl implements DeliveryPersonService {

    private final DeliveryPersonRepository repository;
    private final DeliveryCompanyRepository companyRepository;
    private final DeliveryPersonMapper mapper;

    @Override
    public DeliveryPersonResponse createDeliveryPerson(DeliveryPersonRequest request) {
        if (repository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("Un livreur avec ce numéro de téléphone existe déjà");
        }

        if (request.email() != null && repository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Un livreur avec cet email existe déjà");
        }

        DeliveryPerson person = mapper.toEntity(request);

        DeliveryPerson saved = repository.save(person);
        return mapper.toResponse(saved);
    }

    @Override
    public List<DeliveryPersonResponse> getAllDeliveryPersons() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public DeliveryPersonResponse getDeliveryPersonByTrackingId(UUID trackingId) {
        DeliveryPerson person = repository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Livreur non trouvé"));
        return mapper.toResponse(person);
    }

    @Override
    public List<DeliveryPersonResponse> getDeliveryPersonByCompany(UUID companyTrackingId) {
        DeliveryCompany company = companyRepository.findByTrackingId(companyTrackingId)
                .orElseThrow(() -> new IllegalArgumentException("Compagnie non trouvée"));

        return repository.findByDeliveryCompany(company).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<DeliveryPersonResponse> getDeliveryPersonAvailableByCompany(UUID companyTrackingId) {
        DeliveryCompany company = companyRepository.findByTrackingId(companyTrackingId)
                .orElseThrow(() -> new IllegalArgumentException("Compagnie non trouvée"));

        return repository.findAvailableByCompany(company).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public DeliveryPersonResponse updateDeliveryPerson(UUID trackingId, DeliveryPersonRequest request) {
        DeliveryPerson person = repository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Livreur non trouvé"));

        if (!person.getPhone().equals(request.phone()) && repository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("Un livreur avec ce numéro de téléphone existe déjà");
        }

        if (request.email() != null && !request.email().equals(person.getEmail()) && repository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Un livreur avec cet email existe déjà");
        }

        person.setFirstName(request.firstName());
        person.setLastName(request.lastName());
        person.setPhone(request.phone());
        person.setEmail(request.email());
        person.setVehicleType(request.vehicleType());
        person.setLicenseNumber(request.licenseNumber());

        if(!person.getDeliveryCompany().getTrackingId().equals(request.deliveryCompanyTrackingId())){
            DeliveryPerson tempDeliveryPerson = mapper.toEntity(request);
            person.setDeliveryCompany(tempDeliveryPerson.getDeliveryCompany());
        }

        DeliveryPerson updated = repository.save(person);
        return mapper.toResponse(updated);
    }

    @Override
    public void setAvailability(UUID trackingId, boolean isAvailable) {
        DeliveryPerson person = repository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Livreur non trouvé"));

        person.setIsAvailable(isAvailable);
        repository.save(person);
    }

    @Override
    public void deactivateDeliveryPerson(UUID trackingId) {
        DeliveryPerson person = repository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Livreur non trouvé"));

        person.setIsActive(false);
        person.setIsAvailable(false);
        repository.save(person);
    }

    @Override
    public void deleteDeliveryPerson(UUID trackingId) {
        DeliveryPerson person = repository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Livreur non trouvé"));
        repository.delete(person);
    }
}
