package nitchcorp.backend.titan.shared.securite.user.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequest(

        @Size(min = 2, max = 50)
        String firstName,

        @Size(min = 2, max = 50)
        String lastName,

        @Pattern(
                regexp = "^(\\+?[0-9]{8,15})$",
                message = "Le numéro de téléphone doit être valide (8 à 15 chiffres, optionnel +)"
        )
//
//        @Pattern(
//                regexp = "^([0-9]{10,15})$",
//                message = "Le numéro de téléphone doit être valide (10 à 15 chiffres, optionnel +)"
//        )
        String phone,

        @Email
        @Size(max = 100)
        String email,

        @Size(min = 8, max = 100)
        String password,

        @Size(max = 20)
        String role
) {
}
