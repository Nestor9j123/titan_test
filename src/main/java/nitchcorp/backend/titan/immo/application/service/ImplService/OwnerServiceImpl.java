package nitchcorp.backend.titan.immo.application.service.ImplService;

import lombok.RequiredArgsConstructor;
import nitchcorp.backend.titan.immo.application.dto.requests.OwnerRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.OwnerResponse;
import nitchcorp.backend.titan.immo.application.mapper.OwnerMapper;
import nitchcorp.backend.titan.immo.domain.model.Owner;
import nitchcorp.backend.titan.immo.domain.exceptions.UserNotFoundException;
import nitchcorp.backend.titan.immo.infrastructure.OwnerRepository;
import nitchcorp.backend.titan.immo.application.service.OwnerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OwnerResponse createOwner(OwnerRequest request) {
        if (request.email() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        Owner owner = ownerMapper.toEntity(request);
        owner.setPassword(passwordEncoder.encode(request.password()));
        owner = ownerRepository.save(owner);
        return ownerMapper.toResponse(owner);
    }

    @Override
    public List<OwnerResponse> getAllOwners() {
        return ownerRepository.getAllOwners().stream()
                .map(ownerMapper::toResponse)
                .toList();
    }

    @Override
    public OwnerResponse getOwnerByTrackingId(UUID trackingId) throws UserNotFoundException {
        Owner owner = ownerRepository.getOwnerByTrackingId(trackingId)
                .orElseThrow(() -> new UserNotFoundException("Owner not found with trackingId: " + trackingId));
        return ownerMapper.toResponse(owner);
    }

    @Override
    public OwnerResponse updateOwner(UUID trackingId, OwnerRequest request) throws UserNotFoundException {
        Owner owner = ownerRepository.getOwnerByTrackingId(trackingId)
                .orElseThrow(() -> new UserNotFoundException("Owner not found with trackingId: " + trackingId));
        owner.setFirstName(request.firstName());
        owner.setLastName(request.lastName());
        owner.setEmail(request.email());
        owner.setPhone(request.phone());
        // owner.setAddress(request.address()); // Address not available in User entity
        owner.setCountry(request.country());
        // owner.setOwnershipType(request.ownershipType()); // OwnershipType removed as requested
        owner = ownerRepository.save(owner);
        return ownerMapper.toResponse(owner);
    }

    @Override
    public void deleteOwner(UUID trackingId) throws UserNotFoundException {
        Owner owner = ownerRepository.getOwnerByTrackingId(trackingId)
                .orElseThrow(() -> new UserNotFoundException("Owner not found with trackingId: " + trackingId));
        ownerRepository.delete(owner);
    }
}