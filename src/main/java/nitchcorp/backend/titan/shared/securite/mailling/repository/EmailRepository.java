package nitchcorp.backend.titan.shared.securite.mailling.repository;
import nitchcorp.backend.titan.shared.securite.mailling.entity.EmailConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailConfiguration,Long> {

    EmailConfiguration findFirstByOrderByIdAsc();
}
