package nitchcorp.backend.titan.shared.securite.user.repositories;

import nitchcorp.backend.titan.shared.securite.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByTrackingId(UUID trackingId);
}
