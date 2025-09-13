package nitchcorp.backend.titan.shared.securite.user.dtos.response;

import java.util.List;
import java.util.UUID;

public record UserAuthenticationResponse(
        String token,
        String type,
        Long id,
        String firstName,
        String lastName,
        String phone,
        String email,
        String roles,
        List<String> rolesList,
        UUID trackingId,
        boolean actif
) {
    public UserAuthenticationResponse(String token,
                                      Long id,
                                      String firstName,
                                      String lastName,
                                      String phone,
                                      String email,
                                      String roles,
                                      List<String> rolesList,
                                      UUID trackingId,
                                      boolean actif) {
        this(token, "Bearer", id, firstName, lastName, phone, email, roles, rolesList, trackingId, actif);
    }
}
