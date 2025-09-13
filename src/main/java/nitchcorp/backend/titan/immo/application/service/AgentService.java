package nitchcorp.backend.titan.immo.application.service;

import nitchcorp.backend.titan.immo.application.dto.requests.AgentRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.AgentResponse;
import nitchcorp.backend.titan.immo.domain.exceptions.UserNotFoundException;

import java.util.List;
import java.util.UUID;

public interface AgentService {
    AgentResponse createAgent(AgentRequest request);
    List<AgentResponse> getAllAgents();
    AgentResponse getAgentByTrackingId(UUID trackingId) throws UserNotFoundException;
    AgentResponse updateAgent(UUID trackingId, AgentRequest request) throws UserNotFoundException;
    void deleteAgent(UUID trackingId) throws UserNotFoundException;
}