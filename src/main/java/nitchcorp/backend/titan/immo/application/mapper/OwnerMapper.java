package nitchcorp.backend.titan.immo.application.mapper;

import nitchcorp.backend.titan.immo.application.dto.requests.OwnerRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.OwnerResponse;
import nitchcorp.backend.titan.immo.domain.model.Owner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OwnerMapper {

    public OwnerResponse toResponse(Owner owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        return new OwnerResponse(
                owner.getTrackingId(),
                owner.getId(),
                owner.getFirstName(),
                owner.getLastName(),
                owner.getEmail(),
                owner.getPhone(),
                owner.getCountry(),
                owner.isActif()
        );
    }

    public Owner toEntity(OwnerRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("OwnerRequest cannot be null");
        }
        Owner owner = new Owner();
        owner.setTrackingId(UUID.randomUUID());
        owner.setFirstName(request.firstName());
        owner.setLastName(request.lastName());
        owner.setEmail(request.email());
        owner.setPhone(request.phone());
        owner.setCountry(request.country());
        // Note: address and ownershipType fields removed from OwnerRequest
        return owner;
    }
}