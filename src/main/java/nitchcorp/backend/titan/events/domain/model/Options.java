package nitchcorp.backend.titan.events.domain.model;

import jakarta.persistence.*;
import lombok.*;
import nitchcorp.backend.titan.shared.utils.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "OPTIONS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Options extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true,updatable = false)
    private UUID trackingId;

    @Column(name = "name", nullable = false,length = 50)
    private String name;

}
