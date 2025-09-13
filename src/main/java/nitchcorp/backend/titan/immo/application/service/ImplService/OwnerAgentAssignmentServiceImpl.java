package nitchcorp.backend.titan.immo.application.service.ImplService;

import lombok.RequiredArgsConstructor;
import nitchcorp.backend.titan.immo.application.dto.requests.OwnerAgentAssignmentRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.OwnerAgentAssignmentResponse;
import nitchcorp.backend.titan.immo.application.mapper.OwnerAgentAssignmentMapper;
import nitchcorp.backend.titan.immo.domain.model.Agent;
import nitchcorp.backend.titan.immo.domain.model.Owner;
import nitchcorp.backend.titan.immo.domain.model.OwnerAgentAssignment;
import nitchcorp.backend.titan.immo.domain.model.Property;
import nitchcorp.backend.titan.immo.domain.exceptions.OwnerAgentAssignmentNotFoundException;
import nitchcorp.backend.titan.immo.infrastructure.AgentRepository;
import nitchcorp.backend.titan.immo.infrastructure.OwnerAgentAssignementRepository;
import nitchcorp.backend.titan.immo.infrastructure.OwnerRepository;
import nitchcorp.backend.titan.immo.infrastructure.PropertyRepository;
import nitchcorp.backend.titan.immo.application.service.OwnerAgentAssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerAgentAssignmentServiceImpl implements OwnerAgentAssignmentService {

    private final OwnerAgentAssignementRepository assignmentRepository;
    private final OwnerRepository ownerRepository;
    private final AgentRepository agentRepository;
    private final PropertyRepository propertyRepository;
    private final OwnerAgentAssignmentMapper assignmentMapper;

    @Override
    public OwnerAgentAssignmentResponse createOwnerAgentAssignment(OwnerAgentAssignmentRequest request) {
        Owner owner = ownerRepository.getOwnerByTrackingId(request.ownerId())
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("Owner not found with ID: " + request.ownerId()));
        Agent agent = agentRepository.getAgentByTrackingId(request.agentId())
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("Agent not found with ID: " + request.agentId()));
        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("Property not found with ID: " + request.propertyId()));

        OwnerAgentAssignment assignment = assignmentMapper.toEntity(request, owner, agent, property);
        OwnerAgentAssignment savedAssignment = assignmentRepository.save(assignment);

        return assignmentMapper.toResponse(savedAssignment);
    }

    @Override
    public List<OwnerAgentAssignmentResponse> getAllOwnerAgentAssignments() {
        List<OwnerAgentAssignment> assignments = assignmentRepository.findAll();
        return assignments.stream().map(assignmentMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OwnerAgentAssignmentResponse updateOwnerAgentAssignment(UUID trackingId, OwnerAgentAssignmentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("OwnerAgentAssignmentRequest cannot be null");
        }
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        OwnerAgentAssignment existingAssignment = assignmentRepository.getOwnerAgentAssignmentByTrackingId(trackingId)
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("Assignment not found with trackingId: " + trackingId));

        Owner owner = ownerRepository.getOwnerByTrackingId(request.ownerId())
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("Owner not found with ID: " + request.ownerId()));
        Agent agent = agentRepository.getAgentByTrackingId(request.agentId())
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("Agent not found with ID: " + request.agentId()));
        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("Property not found with ID: " + request.propertyId()));

        existingAssignment.setOwner(owner);
        existingAssignment.setAgent(agent);
        existingAssignment.setProperty(property);
        existingAssignment.setAssignedAt(request.assignedAt());
        existingAssignment.setInstructions(request.instructions());

        OwnerAgentAssignment updatedAssignment = assignmentRepository.save(existingAssignment);
        return assignmentMapper.toResponse(updatedAssignment);
    }

    @Override
    public void deleteOwnerAgentAssignment(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        OwnerAgentAssignment existingAssignment = assignmentRepository.getOwnerAgentAssignmentByTrackingId(trackingId)
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("Assignment not found with trackingId: " + trackingId));

        assignmentRepository.delete(existingAssignment);
    }

    @Override
    public OwnerAgentAssignmentResponse getOwnerAgentAssignmentByTrackingId(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        OwnerAgentAssignment assignment = assignmentRepository.getOwnerAgentAssignmentByTrackingId(trackingId)
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("Assignment not found with trackingId: " + trackingId));

        return assignmentMapper.toResponse(assignment);
    }

    @Override
    public List<OwnerAgentAssignmentResponse> getAssignmentsByOwner(UUID ownerId) {
        if (ownerId == null) {
            throw new IllegalArgumentException("ownerId cannot be null");
        }
        
        List<OwnerAgentAssignment> assignments = assignmentRepository.findByOwnerTrackingId(ownerId);
        return assignments.stream()
                .map(assignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OwnerAgentAssignmentResponse> getAssignmentsByAgent(UUID agentId) {
        if (agentId == null) {
            throw new IllegalArgumentException("agentId cannot be null");
        }
        
        List<OwnerAgentAssignment> assignments = assignmentRepository.findByAgentTrackingId(agentId);
        return assignments.stream()
                .map(assignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OwnerAgentAssignmentResponse getAssignmentByProperty(UUID propertyId) {
        if (propertyId == null) {
            throw new IllegalArgumentException("propertyId cannot be null");
        }
        
        OwnerAgentAssignment assignment = assignmentRepository.findByPropertyTrackingId(propertyId)
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("No assignment found for property: " + propertyId));
        
        return assignmentMapper.toResponse(assignment);
    }

    @Override
    public List<OwnerAgentAssignmentResponse> getAvailableAgents(String country) {
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("country cannot be null or empty");
        }
        
        // Get all active agents in the specified country
        List<Agent> availableAgents = agentRepository.findByCountryAndIsActiveTrue(country);
        
        // Convert to assignment responses (for consistency with other methods)
        // This might need adjustment based on your specific requirements
        return availableAgents.stream()
                .map(agent -> new OwnerAgentAssignmentResponse(
                    null, // trackingId - null for available agents
                    null, // id - null for available agents
                    null, // ownerId - null for available agents
                    agent.getTrackingId(), // agentId - use trackingId instead of ID
                    null, // propertyId - null for available agents
                    null, // assignedAt - null for available agents
                    "Available agent in " + country, // instructions
                    true, // isActive
                    null, // deactivationReason
                    null  // transferReason
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OwnerAgentAssignmentResponse activateAssignment(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }
        
        OwnerAgentAssignment assignment = assignmentRepository.getOwnerAgentAssignmentByTrackingId(trackingId)
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("Assignment not found with trackingId: " + trackingId));
        
        assignment.setIsActive(true);
        assignment.setDeactivationReason(null);
        
        OwnerAgentAssignment updatedAssignment = assignmentRepository.save(assignment);
        return assignmentMapper.toResponse(updatedAssignment);
    }

    @Override
    @Transactional
    public OwnerAgentAssignmentResponse deactivateAssignment(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }
        
        OwnerAgentAssignment assignment = assignmentRepository.getOwnerAgentAssignmentByTrackingId(trackingId)
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("Assignment not found with trackingId: " + trackingId));
        
        assignment.setIsActive(false);
        assignment.setDeactivationReason("Deactivated by owner");
        
        OwnerAgentAssignment updatedAssignment = assignmentRepository.save(assignment);
        return assignmentMapper.toResponse(updatedAssignment);
    }

    @Override
    @Transactional
    public OwnerAgentAssignmentResponse transferAssignment(UUID trackingId, UUID newAgentId, String reason) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }
        if (newAgentId == null) {
            throw new IllegalArgumentException("newAgentId cannot be null");
        }
        
        OwnerAgentAssignment assignment = assignmentRepository.getOwnerAgentAssignmentByTrackingId(trackingId)
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("Assignment not found with trackingId: " + trackingId));
        
        Agent newAgent = agentRepository.getAgentByTrackingId(newAgentId)
                .orElseThrow(() -> new OwnerAgentAssignmentNotFoundException("Agent not found with ID: " + newAgentId));
        
        assignment.setAgent(newAgent);
        assignment.setTransferReason(reason);
        assignment.setAssignedAt(java.time.LocalDateTime.now());
        
        OwnerAgentAssignment updatedAssignment = assignmentRepository.save(assignment);
        return assignmentMapper.toResponse(updatedAssignment);
    }
}