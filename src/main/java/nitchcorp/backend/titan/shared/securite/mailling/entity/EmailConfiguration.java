package nitchcorp.backend.titan.shared.securite.mailling.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import nitchcorp.backend.titan.shared.utils.BaseEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.UUID;



@Builder
@Entity
@Table(name = "mailing_email_configuration")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
public class EmailConfiguration extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID trackingId;
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean smtpAuth;
    private boolean starttlsEnable;

    public EmailConfiguration() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(UUID trackingId) {
        this.trackingId = trackingId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSmtpAuth() {
        return smtpAuth;
    }

    public void setSmtpAuth(boolean smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    public boolean isStarttlsEnable() {
        return starttlsEnable;
    }

    public void setStarttlsEnable(boolean starttlsEnable) {
        this.starttlsEnable = starttlsEnable;
    }
}
