package nitchcorp.backend.titan.shared.securite.user.Mapper;

import nitchcorp.backend.titan.shared.securite.user.dtos.request.UserRequest;
import nitchcorp.backend.titan.shared.securite.user.dtos.response.UserResponse;

import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.utils.constantSecurities.TypeRole;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("UserRequest cannot be null");
        }

        User user = new User();
        user.setTrackingId(UUID.randomUUID());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setEmail(request.email());
        user.setPassword(request.password());

        // Conversion String -> TypeRole
        if (request.role() != null) {
            user.setRole(TypeRole.valueOf(request.role().toUpperCase()));
        }

        user.setActif(false); // par défaut false
        return user;
    }

    public UserResponse toResponse(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return new UserResponse(
                user.getTrackingId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getEmail(),
                user.isActif(),
                user.getRole() != null ? user.getRole().name() : null
        );
    }

    public UserResponse updateEntity(User user, UserRequest request) {
        if (user == null || request == null) {
            throw new IllegalArgumentException("User and UserRequest cannot be null");
        }

        user.setFirstName(request.firstName() != null ? request.firstName() : user.getFirstName());
        user.setLastName(request.lastName() != null ? request.lastName() : user.getLastName());
        user.setPhone(request.phone() != null ? request.phone() : user.getPhone());
        user.setEmail(request.email() != null ? request.email() : user.getEmail());
        user.setPassword(request.password() != null ? request.password() : user.getPassword());

        if (request.role() != null) {
            user.setRole(TypeRole.valueOf(request.role().toUpperCase()));
        }

        return toResponse(user);
    }
}
