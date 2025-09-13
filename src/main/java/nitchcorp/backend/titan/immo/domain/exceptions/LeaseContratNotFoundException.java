package nitchcorp.backend.titan.immo.domain.exceptions;

import org.springframework.modulith.NamedInterface;

@NamedInterface
public class LeaseContratNotFoundException extends RuntimeException {
    public LeaseContratNotFoundException(String message) {
        super(message);
    }
}
