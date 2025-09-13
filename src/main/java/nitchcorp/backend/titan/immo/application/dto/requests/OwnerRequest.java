package nitchcorp.backend.titan.immo.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OwnerRequest(
        @NotNull(message = "Le prénom est requis") 
        @NotBlank(message = "Le prénom ne peut pas être vide")
        @JsonProperty("firstName") String firstName,
        
        @NotNull(message = "Le nom est requis") 
        @NotBlank(message = "Le nom ne peut pas être vide")
        @JsonProperty("lastName") String lastName,
        
        @NotNull(message = "L'email est requis") 
        @Email(message = "Format d'email invalide")
        @JsonProperty("email") String email,
        
        @NotNull(message = "Le mot de passe est requis")
        @NotBlank(message = "Le mot de passe ne peut pas être vide")
        @JsonProperty("password") String password,
        
        @JsonProperty("phone") String phone,
        
        @NotNull(message = "Le pays est requis") 
        @NotBlank(message = "Le pays ne peut pas être vide")
        @JsonProperty("country") String country
) {}