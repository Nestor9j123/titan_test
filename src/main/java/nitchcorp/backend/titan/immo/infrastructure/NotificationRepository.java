package nitchcorp.backend.titan.immo.infrastructure;

import nitchcorp.backend.titan.immo.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.trackingId = :trackingId")
    Optional<Notification> getNotificationByTrackingId(UUID trackingId);

    @Query("SELECT n FROM Notification n  order by n.id DESC")
    List<Notification> getAllNotifications();
}
