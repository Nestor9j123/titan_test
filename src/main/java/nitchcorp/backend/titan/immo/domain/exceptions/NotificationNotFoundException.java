package nitchcorp.backend.titan.immo.domain.exceptions;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(String message) {
        super(message);
    }
}
