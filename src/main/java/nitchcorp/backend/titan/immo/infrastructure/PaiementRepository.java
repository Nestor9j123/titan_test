package nitchcorp.backend.titan.immo.infrastructure;

import nitchcorp.backend.titan.immo.domain.model.Paiement;
import nitchcorp.backend.titan.immo.domain.enums.PaiementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    @Query("SELECT p FROM Paiement p WHERE p.trackingId = :trackingId")
    Optional<Paiement> getPaiementByTrackingId(UUID trackingId);

    @Query("SELECT p FROM Paiement p ORDER BY p.id DESC")
    List<Paiement> getAllPaiements();

    @Query("SELECT p FROM Paiement p WHERE p.leaseContract.trackingId = :leaseContractTrackingId")
    List<Paiement> findByLeaseContractTrackingId(@Param("leaseContractTrackingId") UUID leaseContractTrackingId);

    @Query("SELECT p FROM Paiement p WHERE p.customer.trackingId = :customerTrackingId")
    List<Paiement> findByCustomerTrackingId(@Param("customerTrackingId") UUID customerTrackingId);

    @Query("SELECT p FROM Paiement p WHERE p.leaseContract.property.owner.trackingId = :ownerId")
    List<Paiement> findByOwnerId(@Param("ownerId") UUID ownerId);

    List<Paiement> findByStatus(PaiementStatus status);

    @Query("SELECT p FROM Paiement p WHERE p.dueDate < CURRENT_DATE AND p.status = 'UNPAID'")
    List<Paiement> findOverduePayments();
}
