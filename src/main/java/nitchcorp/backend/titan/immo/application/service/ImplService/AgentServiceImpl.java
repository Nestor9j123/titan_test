package nitchcorp.backend.titan.immo.application.service.ImplService;

import lombok.RequiredArgsConstructor;
import nitchcorp.backend.titan.immo.application.dto.requests.AgentRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.AgentResponse;
import nitchcorp.backend.titan.immo.application.mapper.AgentMapper;
import nitchcorp.backend.titan.immo.domain.model.Agent;
import nitchcorp.backend.titan.immo.domain.exceptions.UserNotFoundException;
import nitchcorp.backend.titan.immo.infrastructure.AgentRepository;
import nitchcorp.backend.titan.immo.application.service.AgentService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final AgentMapper agentMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AgentResponse createAgent(AgentRequest request) {
        if (request.email() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        Agent agent = agentMapper.toEntity(request);
        agent.setPassword(passwordEncoder.encode(request.password()));
        agent = agentRepository.save(agent);
        return agentMapper.toResponse(agent);
    }

    @Override
    public List<AgentResponse> getAllAgents() {
        return agentRepository.getAllAgents().stream()
                .map(agentMapper::toResponse)
                .toList();
    }

    @Override
    public AgentResponse getAgentByTrackingId(UUID trackingId) throws UserNotFoundException {
        Agent agent = agentRepository.getAgentByTrackingId(trackingId)
                .orElseThrow(() -> new UserNotFoundException("Agent not found with trackingId: " + trackingId));
        return agentMapper.toResponse(agent);
    }

    @Override
    public AgentResponse updateAgent(UUID trackingId, AgentRequest request) throws UserNotFoundException {
        Agent agent = agentRepository.getAgentByTrackingId(trackingId)
                .orElseThrow(() -> new UserNotFoundException("Agent not found with trackingId: " + trackingId));
        agent.setFirstName(request.firstName());
        agent.setLastName(request.lastName());
        agent.setEmail(request.email());
        agent.setPhone(request.phone());
        agent.setCountry(request.country());
        // Removed setAddress and setLicenseNumber as requested
        agent = agentRepository.save(agent);
        return agentMapper.toResponse(agent);
    }

    @Override
    public void deleteAgent(UUID trackingId) throws UserNotFoundException {
        Agent agent = agentRepository.getAgentByTrackingId(trackingId)
                .orElseThrow(() -> new UserNotFoundException("Agent not found with trackingId: " + trackingId));
        agentRepository.delete(agent);
    }
}