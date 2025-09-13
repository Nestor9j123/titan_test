package nitchcorp.backend.titan.events.domain.model;

import jakarta.persistence.*;
import lombok.*;
import nitchcorp.backend.titan.shared.utils.BaseEntity;
import nitchcorp.backend.titan.shared.securite.user.entities.User;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "VOTES")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Votes extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID trackingId;
    private String question;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "votes_id")
    private List<Options> options;
    private boolean statusVote;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Events event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

}