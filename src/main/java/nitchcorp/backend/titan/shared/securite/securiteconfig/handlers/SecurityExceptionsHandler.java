package nitchcorp.backend.titan.shared.securite.securiteconfig.handlers;



import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.persistence.NoResultException;
import nitchcorp.backend.titan.shared.securite.exception.UserAlreadyExistException;
import nitchcorp.backend.titan.shared.securite.exception.UserNotFoundException;
import nitchcorp.backend.titan.shared.securite.exception.response.HttpErrorResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.management.relation.RoleNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class SecurityExceptionsHandler implements ErrorController {
    private static final String ACCOUNT_LOCKED = "Vôtre compte a été bloqué. Veuillez contacter l'administration";
    private static final String METHOD_IS_NOT_ALLOWED = "Cette méthode de demande n'est pas autorisée sur ce point de terminaison. Veuillez envoyer une demande %s";
    private static final String INTERNAL_SERVER_ERROR_MSG = "Une erreur s'est produite lors du traitement de la demande";
    private static final String INCORRECT_CREDENTIALS = "Mot de passe incorrect. Veuillez réessayer";
    private static final String ACCOUNT_DISABLED = "Votre compte a été désactivé. S'il s'agit d'une erreur, veuillez contacter l'administration";
    private static final String ERROR_PROCESSING_FILE = "Une erreur s'est produite lors du traitement du fichier";
    private static final String NOT_ENOUGH_PERMISSION = "Vous n'avez pas assez d'autorisation";
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpErrorResponse> accountDisabledException() {
        return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpErrorResponse> badCredentialsException() {
        return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpErrorResponse> accessDeniedException() {
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpErrorResponse> lockedException() {
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<HttpErrorResponse> tokenExpiredException(ExpiredJwtException exception) {
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<HttpErrorResponse> malformedJwtException(MalformedJwtException exception) {
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpErrorResponse> noHandlerFoundException(NoHandlerFoundException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, "This page was not found");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        LOGGER.error(exception.getAllErrors().toString());
        return createValidationHttpResponse(exception.getFieldErrors());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpErrorResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod httpMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, httpMethod));
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpErrorResponse> notFoundException(NoResultException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<HttpErrorResponse> roleNotFoundException(RoleNotFoundException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpErrorResponse> userNotFoundException(UserNotFoundException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }


    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<HttpErrorResponse> userAlreadyExistException(UserAlreadyExistException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }



 /*   @ExceptionHandler(ParametreNotValidate.class)
    public ResponseEntity<HttpErrorResponse> ParametreNotValidate(ParametreNotValidate exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }*/


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<HttpErrorResponse> constraintViolationException(ConstraintViolationException exception) {
        LOGGER.error(exception.getMessage());
        LOGGER.error(exception.getConstraintName());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<HttpErrorResponse> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpErrorResponse> ioException(IOException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpErrorResponse> internalServerErrorException(Exception exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }


    private ResponseEntity<HttpErrorResponse> createHttpResponse(HttpStatus status, String message) {
        HttpErrorResponse httpResponse = new HttpErrorResponse();
        httpResponse.setStatus(status);
        httpResponse.setStatusCode(status.value());
        httpResponse.setMessage(message);
        httpResponse.setReason(status.getReasonPhrase());
        return new ResponseEntity<>(httpResponse, status);
    }

    private ResponseEntity<HttpErrorResponse> createValidationHttpResponse(List<FieldError> errors) {
        HttpErrorResponse httpResponse = new HttpErrorResponse();
        httpResponse.setStatus(BAD_REQUEST);
        httpResponse.setStatusCode(BAD_REQUEST.value());
        httpResponse.setMessage("Validation errors");
        httpResponse.setReason(BAD_REQUEST.getReasonPhrase());
        httpResponse.setValidations(errors);
        return new ResponseEntity<>(httpResponse, BAD_REQUEST);
    }
}
