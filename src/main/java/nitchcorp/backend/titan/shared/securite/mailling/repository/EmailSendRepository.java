package nitchcorp.backend.titan.shared.securite.mailling.repository;
import nitchcorp.backend.titan.shared.securite.mailling.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailSendRepository extends JpaRepository<Email,Long>  {
}
