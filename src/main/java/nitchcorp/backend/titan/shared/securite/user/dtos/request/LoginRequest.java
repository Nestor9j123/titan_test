package nitchcorp.backend.titan.shared.securite.user.dtos.request;


import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public record LoginRequest(
        @Email
        String email,

        String password
) {}
