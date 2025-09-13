package nitchcorp.backend.titan.immo.domain.exceptions;

public class VisitNotFoundException extends RuntimeException {
    public VisitNotFoundException(String message) {
        super(message);
    }
}
