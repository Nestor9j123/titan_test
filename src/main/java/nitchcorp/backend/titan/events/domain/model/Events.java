package nitchcorp.backend.titan.events.domain.model;

import jakarta.persistence.*;
import lombok.*;
import nitchcorp.backend.titan.shared.utils.BaseEntity;
import nitchcorp.backend.titan.shared.securite.user.entities.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Entity
@Table(name = "EVENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Events extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true,updatable = false)
    private UUID trackingId;

    @Column(nullable = false,length = 50)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @ElementCollection
    private List<String> images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;
}

