package nitchcorp.backend.titan.immo.application.dto.responses;

import java.util.UUID;

public record CustomerResponse(
        UUID trackingId,
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String country,
        boolean isActif,
        String preferredContactMethod
) {}