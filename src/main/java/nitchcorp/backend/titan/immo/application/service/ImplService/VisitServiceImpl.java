package nitchcorp.backend.titan.immo.application.service.ImplService;

import lombok.RequiredArgsConstructor;
import nitchcorp.backend.titan.immo.application.dto.requests.VisitRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.VisitResponse;
import nitchcorp.backend.titan.immo.application.mapper.VisitMapper;
import nitchcorp.backend.titan.immo.domain.model.Agent;
import nitchcorp.backend.titan.immo.domain.model.Customer;
import nitchcorp.backend.titan.immo.domain.model.Property;
import nitchcorp.backend.titan.immo.domain.model.Visit;
import nitchcorp.backend.titan.immo.domain.enums.VisitStatus;
import nitchcorp.backend.titan.immo.domain.exceptions.VisitNotFoundException;
import nitchcorp.backend.titan.immo.infrastructure.AgentRepository;
import nitchcorp.backend.titan.immo.infrastructure.CustomerRepository;
import nitchcorp.backend.titan.immo.infrastructure.PropertyRepository;
import nitchcorp.backend.titan.immo.infrastructure.VisitRepository;
import nitchcorp.backend.titan.immo.application.service.VisitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final PropertyRepository propertyRepository;
    private final AgentRepository agentRepository;
    private final CustomerRepository customerRepository;
    private final VisitMapper visitMapper;

    @Override
    public VisitResponse createVisit(VisitRequest request) {
        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new VisitNotFoundException("Property not found with ID: " + request.propertyId()));
        Agent agent = agentRepository.findById(request.agentId())
                .orElseThrow(() -> new VisitNotFoundException("Agent not found with ID: " + request.agentId()));
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new VisitNotFoundException("Customer not found with ID: " + request.customerId()));

        Visit visit = visitMapper.toEntity(request, property, agent, customer);
        visit.setStatus(VisitStatus.PLANNED);
        Visit savedVisit = visitRepository.save(visit);

        return visitMapper.toResponse(savedVisit);
    }

    @Override
    public List<VisitResponse> getAllVisits() {
        List<Visit> visits = visitRepository.findAll();
        return visits.stream().map(visitMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VisitResponse updateVisit(UUID trackingId, VisitRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("VisitRequest cannot be null");
        }
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        Visit existingVisit = visitRepository.getVisitByTrackingId(trackingId)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found with trackingId: " + trackingId));

        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new VisitNotFoundException("Property not found with ID: " + request.propertyId()));
        Agent agent = agentRepository.findById(request.agentId())
                .orElseThrow(() -> new VisitNotFoundException("Agent not found with ID: " + request.agentId()));
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new VisitNotFoundException("Customer not found with ID: " + request.customerId()));

        existingVisit.setProperty(property);
        existingVisit.setAgent(agent);
        existingVisit.setCustomer(customer);
        existingVisit.setVisitDate(request.visitDate());
        existingVisit.setStatus(request.status());
        existingVisit.setComments(request.comments());

        Visit updatedVisit = visitRepository.save(existingVisit);
        return visitMapper.toResponse(updatedVisit);
    }

    @Override
    public void deleteVisit(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        Visit existingVisit = visitRepository.getVisitByTrackingId(trackingId)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found with trackingId: " + trackingId));

        visitRepository.delete(existingVisit);
    }

    @Override
    public VisitResponse getVisitByTrackingId(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        Visit visit = visitRepository.getVisitByTrackingId(trackingId)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found with trackingId: " + trackingId));

        return visitMapper.toResponse(visit);
    }

    @Override
    public List<VisitResponse> getVisitsByAgent(UUID agentId) {
        return visitRepository.findByAgentTrackingId(agentId).stream()
                .map(visitMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitResponse> getVisitsByCustomer(UUID customerId) {
        return visitRepository.findByCustomerTrackingId(customerId).stream()
                .map(visitMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitResponse> getVisitsByProperty(UUID propertyId) {
        return visitRepository.findByPropertyTrackingId(propertyId).stream()
                .map(visitMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VisitResponse confirmVisit(UUID trackingId) {
        Visit visit = visitRepository.getVisitByTrackingId(trackingId)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found"));
        visit.setStatus(VisitStatus.CONFIRMED);
        return visitMapper.toResponse(visitRepository.save(visit));
    }

    @Override
    public VisitResponse cancelVisit(UUID trackingId, String reason) {
        Visit visit = visitRepository.getVisitByTrackingId(trackingId)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found"));
        visit.setStatus(VisitStatus.CANCELLED);
        visit.setComments(reason);
        return visitMapper.toResponse(visitRepository.save(visit));
    }

    @Override
    public VisitResponse completeVisit(UUID trackingId, String comments) {
        Visit visit = visitRepository.getVisitByTrackingId(trackingId)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found"));
        visit.setStatus(VisitStatus.COMPLETED);
        visit.setComments(comments);
        return visitMapper.toResponse(visitRepository.save(visit));
    }

    @Override
    public List<String> getAgentAvailability(UUID agentId, String startDate, String endDate) {
        // Logique de disponibilité de l'agent à implémenter
        return Collections.emptyList();
    }

    @Override
    public List<VisitResponse> getVisitsByStatus(String status) {
        try {
            VisitStatus visitStatus = VisitStatus.valueOf(status.toUpperCase());
            return visitRepository.findByStatus(visitStatus).stream()
                    .map(visitMapper::toResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }

    @Override
    public VisitResponse rescheduleVisit(UUID trackingId, String newDateTime, String reason) {
        Visit visit = visitRepository.getVisitByTrackingId(trackingId)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found"));

        LocalDateTime parsedDateTime = LocalDateTime.parse(newDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        visit.setVisitDate(parsedDateTime);
        visit.setStatus(VisitStatus.RESCHEDULED);
        visit.setComments(reason);

        return visitMapper.toResponse(visitRepository.save(visit));
    }
}