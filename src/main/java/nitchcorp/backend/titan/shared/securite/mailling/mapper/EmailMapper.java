package nitchcorp.backend.titan.shared.securite.mailling.mapper;


import lombok.AllArgsConstructor;
import nitchcorp.backend.titan.shared.securite.mailling.dto.request.EmailRequest;
import nitchcorp.backend.titan.shared.securite.mailling.dto.response.EmailResponse;
import nitchcorp.backend.titan.shared.securite.mailling.entity.Email;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EmailMapper {


    public EmailResponse toDto(Email email) {
        EmailResponse response = new EmailResponse();
        BeanUtils.copyProperties(email, response);
        return response;
    }

    public Email toEntity(EmailRequest request) {
        Email email = new Email();
        BeanUtils.copyProperties(request, email);
        return email;
    }

    public Email toEntity(EmailRequest request, Email email) {
        BeanUtils.copyProperties(request, email);
        return email;
    }

}
