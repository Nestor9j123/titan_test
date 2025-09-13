package nitchcorp.backend.titan.shared.securite.mailling.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;


@Entity
@Table(name = "mailing_emails")
@Builder
@ToString
@AllArgsConstructor
public class Email implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "mail_from")
    private String mailFrom;

    @Column(name = "mail_to")
    private String mailTo;

    @Column(name = "mail_cc")
    private String mailCc;

    @Column(name = "mail_bcc")
    private String mailBcc;

    @Column(name = "mail_subject")
    private String mailSubject;

    @Column(name = "mail_content")
    private String mailContent;


    public Email() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getMailCc() {
        return mailCc;
    }

    public void setMailCc(String mailCc) {
        this.mailCc = mailCc;
    }

    public String getMailBcc() {
        return mailBcc;
    }

    public void setMailBcc(String mailBcc) {
        this.mailBcc = mailBcc;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailContent() {
        return mailContent;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }
}
