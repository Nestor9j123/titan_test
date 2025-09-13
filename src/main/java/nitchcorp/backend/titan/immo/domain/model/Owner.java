package nitchcorp.backend.titan.immo.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import nitchcorp.backend.titan.shared.securite.user.entities.User;

@Entity
@DiscriminatorValue("OWNER")
@Getter
@Setter
public class Owner extends User {
    // Simplified Owner entity - inherits all user properties from User
    // No additional specific attributes needed as requested
}