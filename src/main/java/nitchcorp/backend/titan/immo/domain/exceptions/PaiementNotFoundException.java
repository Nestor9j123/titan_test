package nitchcorp.backend.titan.immo.domain.exceptions;

public class PaiementNotFoundException extends RuntimeException {
    public PaiementNotFoundException(String message) {
        super(message);
    }
}
