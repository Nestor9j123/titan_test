package nitchcorp.backend.titan.immo.infrastructure;

import nitchcorp.backend.titan.immo.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c WHERE c.trackingId = :trackingId")
    Optional<Customer> getCustomerByTrackingId(UUID trackingId);

    @Query("SELECT c FROM Customer c order by c.id DESC")
    List<Customer> getAllCustomers();
}
