package nitchcorp.backend.titan.events.domain.exception;

import org.springframework.modulith.NamedInterface;

@NamedInterface
public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String message) {
        super(message);
    }
}
