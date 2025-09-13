package nitchcorp.backend.titan.immo.application.mapper;

import nitchcorp.backend.titan.immo.application.dto.requests.AgentRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.AgentResponse;
import nitchcorp.backend.titan.immo.domain.model.Agent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgentMapper {

    public AgentResponse toResponse(Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("Agent cannot be null");
        }
        return new AgentResponse(
                agent.getTrackingId(),
                agent.getId(),
                agent.getFirstName(),
                agent.getLastName(),
                agent.getEmail(),
                agent.getPhone(),
                agent.getCountry(),
                agent.isActif()
        );
    }

    public Agent toEntity(AgentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("AgentRequest cannot be null");
        }
        Agent agent = new Agent();
        agent.setTrackingId(UUID.randomUUID());
        agent.setFirstName(request.firstName());
        agent.setLastName(request.lastName());
        agent.setEmail(request.email());
        agent.setPhone(request.phone());
        agent.setCountry(request.country());
        return agent;
    }
}
