package nitchcorp.backend.titan.shared.securite.securiteconfig.filters;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nitchcorp.backend.titan.shared.securite.exception.response.HttpErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;


import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static nitchcorp.backend.titan.shared.utils.constantSecurities.JavaConstant.FORBIDDEN_MESSAGE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2) throws IOException {
        System.out.println("JWT ENTRY req methode" +request.getMethod());
        System.out.println("JWT ENTRY req url" +request.getRequestURI());
        System.out.println("JWT ENTRY req token" +request.getHeader("Authorization"));
        HttpErrorResponse errorResponse = new HttpErrorResponse();
        errorResponse.setStatus(FORBIDDEN);
        errorResponse.setStatusCode(FORBIDDEN.value());
        errorResponse.setReason(FORBIDDEN.getReasonPhrase());
        errorResponse.setMessage(FORBIDDEN_MESSAGE);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, errorResponse);
        outputStream.flush();
    }
}
