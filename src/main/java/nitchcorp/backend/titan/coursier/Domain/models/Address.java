package nitchcorp.backend.titan.coursier.Domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Address {

    @Column(nullable = false)
    private String streetAddress;

    @Column(nullable = true)
    private String city;

    @Column(nullable = true)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String additionalInfo;

    private Double latitude;

    private Double longitude;

}
