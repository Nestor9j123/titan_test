package nitchcorp.backend.titan.immo.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import nitchcorp.backend.titan.shared.securite.user.entities.User;

@Entity
@DiscriminatorValue("AGENT")
@Getter
@Setter
public class Agent extends User {
}
