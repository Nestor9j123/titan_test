package nitchcorp.backend.titan.coursier.Application.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContactInfoDTO(
        @NotBlank
        String contactName,
        @NotBlank
        @Pattern(regexp = "^\\+?[0-9]{8,15}$")
        String contactPhone
) {
        @JsonCreator
        public ContactInfoDTO(
                @JsonProperty("contactName") String contactName,
                @JsonProperty("contactPhone") String contactPhone) {
                this.contactName = contactName;
                this.contactPhone = contactPhone;
        }
}
