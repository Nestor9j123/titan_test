package nitchcorp.backend.titan.shared.securite.mailling.service;


import nitchcorp.backend.titan.shared.securite.mailling.dto.request.EmailRequest;
import nitchcorp.backend.titan.shared.securite.mailling.dto.response.EmailResponse;

public interface EmailService {

    EmailResponse send(EmailRequest request, String template);
}
