package nitchcorp.backend.titan.coursier.Application.services.ImplService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nitchcorp.backend.titan.coursier.Application.dtos.requests.DeliveryCompanyRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.DeliveryCompanyResponse;
import nitchcorp.backend.titan.coursier.Application.mappers.DeliveryCompanyMapper;
import nitchcorp.backend.titan.coursier.Domain.models.DeliveryCompany;
import nitchcorp.backend.titan.coursier.Infrastructure.DeliveryCompanyRepository;
import nitchcorp.backend.titan.coursier.Application.services.DeliveryCompanyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class DeliveryCompanyServiceImpl implements DeliveryCompanyService {

    private final DeliveryCompanyRepository repository;
    private final DeliveryCompanyMapper mapper;


    @Override
    public DeliveryCompanyResponse createCompany(DeliveryCompanyRequest request) {
        if (repository.existsByName(request.name())) {
            throw new IllegalArgumentException("Une compagnie avec ce nom existe déjà");
        }

        if (repository.existsByContactEmail(request.contactEmail())) {
            throw new IllegalArgumentException("Une compagnie avec cet email existe déjà");
        }

        DeliveryCompany company = mapper.toEntity(request);
        DeliveryCompany saved = repository.save(company);
        return mapper.toResponse(saved);
    }

    @Override
    public List<DeliveryCompanyResponse> getAllCompanies() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<DeliveryCompanyResponse> getAllCompaniesActive() {
        return repository.findAllActive().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public DeliveryCompanyResponse getCompanyByTrackingId(UUID trackingId) {
        DeliveryCompany company = repository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Compagnie non trouvée"));
        return mapper.toResponse(company);
    }

    @Override
    public DeliveryCompanyResponse updateCompany(UUID trackingId, DeliveryCompanyRequest request) {
        DeliveryCompany company = repository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Compagnie non trouvée"));

        if (!company.getName().equals(request.name()) && repository.existsByName(request.name())) {
            throw new IllegalArgumentException("Une compagnie avec ce nom existe déjà");
        }

        if (!company.getContactEmail().equals(request.contactEmail()) && repository.existsByContactEmail(request.contactEmail())) {
            throw new IllegalArgumentException("Une compagnie avec cet email existe déjà");
        }

        company.setName(request.name());
        company.setDescription(request.description());
        company.setContactEmail(request.contactEmail());
        company.setContactPhone(request.contactPhone());
        company.setAddress(request.address());

        DeliveryCompany updated = repository.save(company);
        return mapper.toResponse(updated);
    }

    @Override
    public void deactivateCompany(UUID trackingId) {
        DeliveryCompany company = repository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Compagnie non trouvée"));

        company.setIsActive(false);
        repository.save(company);
    }

    @Override
    public void activateCompany(UUID trackingId) {
        DeliveryCompany company = repository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Compagnie non trouvée"));

        company.setIsActive(true);
        repository.save(company);
    }

    @Override
    public void deleteCompany(UUID trackingId) {
        if (!repository.findByTrackingId(trackingId).isPresent()) {
            throw new EntityNotFoundException("Compagnie non trouvé avec l'ID: " + trackingId);
        }
        repository.deleteByTrackingId(trackingId);
    }
}
