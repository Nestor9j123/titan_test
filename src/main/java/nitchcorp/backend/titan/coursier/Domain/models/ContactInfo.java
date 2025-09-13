package nitchcorp.backend.titan.coursier.Domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ContactInfo {
    @Column(name = "contact_name", nullable = false)
    private String contactName;

    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

}
