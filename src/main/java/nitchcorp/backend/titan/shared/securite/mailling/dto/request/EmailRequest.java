package nitchcorp.backend.titan.shared.securite.mailling.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Setter
@Getter
@Builder
@AllArgsConstructor
public class EmailRequest implements Serializable {

    private String mailFrom;

    private String mailTo;

    private String mailCc;

    private String mailBcc;

    private String mailSubject;

    private String mailContent;

    private String nom;

    private String entreprise;

    private String plan;

    private String username;

    private String password;

    private String lien;

    private String endDate;

    private String startDate;

    private String montant;

    private String contact;


    public EmailRequest() {
    }

}
