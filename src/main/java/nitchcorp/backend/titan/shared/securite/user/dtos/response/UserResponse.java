package nitchcorp.backend.titan.shared.securite.user.dtos.response;

import java.util.UUID;

public record UserResponse(
        UUID trackingId ,
        String  firstName ,
        String lastName ,
        String phone ,
        String email ,
        boolean actif,
        String role

) {}
