package nitchcorp.backend.titan.shared.securite.user.services;

import nitchcorp.backend.titan.shared.securite.user.dtos.request.LoginRequest;
import nitchcorp.backend.titan.shared.securite.user.dtos.request.UserRequest;
import nitchcorp.backend.titan.shared.securite.user.dtos.response.UserAuthenticationResponse;
import nitchcorp.backend.titan.shared.securite.user.dtos.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserAuthenticationResponse authenticate(LoginRequest loginDTO);
    UserResponse createUser(UserRequest request);
    UserResponse updateUserEtat(UUID trackingId, boolean etat);

}
