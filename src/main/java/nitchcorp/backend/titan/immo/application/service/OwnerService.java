package nitchcorp.backend.titan.immo.application.service;

import nitchcorp.backend.titan.immo.application.dto.requests.OwnerRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.OwnerResponse;
import nitchcorp.backend.titan.immo.domain.exceptions.UserNotFoundException;

import java.util.List;
import java.util.UUID;

public interface OwnerService {
    OwnerResponse createOwner(OwnerRequest request);
    List<OwnerResponse> getAllOwners();
    OwnerResponse getOwnerByTrackingId(UUID trackingId) throws UserNotFoundException;
    OwnerResponse updateOwner(UUID trackingId, OwnerRequest request) throws UserNotFoundException;
    void deleteOwner(UUID trackingId) throws UserNotFoundException;
}