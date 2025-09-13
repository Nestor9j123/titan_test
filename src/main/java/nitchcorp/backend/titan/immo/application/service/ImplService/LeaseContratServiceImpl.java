package nitchcorp.backend.titan.immo.application.service.ImplService;

import lombok.RequiredArgsConstructor;
import nitchcorp.backend.titan.immo.application.dto.requests.LeaseContratRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.LeaseContratResponse;
import nitchcorp.backend.titan.immo.application.mapper.LeaseContratMapper;
import nitchcorp.backend.titan.immo.domain.model.Agent;
import nitchcorp.backend.titan.immo.domain.model.Customer;
import nitchcorp.backend.titan.immo.domain.model.LeaseContrat;
import nitchcorp.backend.titan.immo.domain.model.Property;
import nitchcorp.backend.titan.immo.domain.exceptions.LeaseContratNotFoundException;
import nitchcorp.backend.titan.immo.infrastructure.AgentRepository;
import nitchcorp.backend.titan.immo.infrastructure.CustomerRepository;
import nitchcorp.backend.titan.immo.infrastructure.LeaseContractRepository;
import nitchcorp.backend.titan.immo.infrastructure.PropertyRepository;
import nitchcorp.backend.titan.immo.application.service.LeaseContratService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaseContratServiceImpl implements LeaseContratService {

    private final LeaseContractRepository leaseContratRepository;
    private final PropertyRepository propertyRepository;
    private final CustomerRepository customerRepository;
    private final AgentRepository agentRepository;
    private final LeaseContratMapper leaseContratMapper;

    @Override
    public LeaseContratResponse createLeaseContrat(LeaseContratRequest request) {
        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new LeaseContratNotFoundException("Property not found with ID: " + request.propertyId()));
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new LeaseContratNotFoundException("Customer not found with ID: " + request.customerId()));
        Agent agent = agentRepository.findById(request.agentId())
                .orElseThrow(() -> new LeaseContratNotFoundException("Agent not found with ID: " + request.agentId()));

        LeaseContrat leaseContrat = leaseContratMapper.toEntity(request, property, customer, agent);
        LeaseContrat savedLeaseContrat = leaseContratRepository.save(leaseContrat);

        return leaseContratMapper.toResponse(savedLeaseContrat);
    }

    @Override
    public List<LeaseContratResponse> getAllLeaseContrats() {
        List<LeaseContrat> leaseContrats = leaseContratRepository.findAll();
        return leaseContrats.stream().map(leaseContratMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LeaseContratResponse updateLeaseContrat(UUID trackingId, LeaseContratRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("LeaseContratRequest cannot be null");
        }
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        LeaseContrat existingLeaseContrat = leaseContratRepository.getLeaseContractByTrackingId(trackingId)
                .orElseThrow(() -> new LeaseContratNotFoundException("LeaseContrat not found with trackingId: " + trackingId));

        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new LeaseContratNotFoundException("Property not found with ID: " + request.propertyId()));
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new LeaseContratNotFoundException("Customer not found with ID: " + request.customerId()));
        Agent agent = agentRepository.findById(request.agentId())
                .orElseThrow(() -> new LeaseContratNotFoundException("Agent not found with ID: " + request.agentId()));

        existingLeaseContrat.setProperty(property);
        existingLeaseContrat.setCustomer(customer);
        existingLeaseContrat.setAgent(agent);
        existingLeaseContrat.setStartDate(request.startDate());
        existingLeaseContrat.setEndDate(request.endDate());
        existingLeaseContrat.setRentAmount(request.rentAmount());
        existingLeaseContrat.setDepositAmount(request.depositAmount());
        existingLeaseContrat.setContractDocument(request.contractDocument());
        existingLeaseContrat.setStatus(request.status());

        LeaseContrat updatedLeaseContrat = leaseContratRepository.save(existingLeaseContrat);
        return leaseContratMapper.toResponse(updatedLeaseContrat);
    }

    @Override
    public void deleteLeaseContrat(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        LeaseContrat existingLeaseContrat = leaseContratRepository.getLeaseContractByTrackingId(trackingId)
                .orElseThrow(() -> new LeaseContratNotFoundException("LeaseContrat not found with trackingId: " + trackingId));

        leaseContratRepository.delete(existingLeaseContrat);
    }

    @Override
    public LeaseContratResponse getLeaseContratByTrackingId(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        LeaseContrat leaseContrat = leaseContratRepository.getLeaseContractByTrackingId(trackingId)
                .orElseThrow(() -> new LeaseContratNotFoundException("LeaseContrat not found with trackingId: " + trackingId));

        return leaseContratMapper.toResponse(leaseContrat);
    }

    @Override
    public List<LeaseContratResponse> getLeaseContratsByProperty(UUID propertyId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LeaseContratResponse> getLeaseContratsByCustomer(UUID customerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LeaseContratResponse> getLeaseContratsByOwner(UUID ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LeaseContratResponse> getLeaseContratsByAgent(UUID agentId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LeaseContratResponse> getActiveLeaseContracts() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LeaseContratResponse> getExpiringLeaseContracts(int daysBeforeExpiry) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public LeaseContratResponse signLeaseContract(UUID trackingId, String signerType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public LeaseContratResponse terminateLeaseContract(UUID trackingId, String reason, String terminationDate) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public LeaseContratResponse renewLeaseContract(UUID trackingId, String newEndDate, Double newRent) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public LeaseContratResponse confirmDeposit(UUID trackingId, Double depositAmount) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public byte[] generateLeaseContractPdf(UUID trackingId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LeaseContratResponse> getLeaseContractHistory(UUID propertyId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}