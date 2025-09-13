package nitchcorp.backend.titan.immo.domain.exceptions;

public class PropertyNotFoundException extends RuntimeException {
  public PropertyNotFoundException(String message) {
    super(message);
  }
}
