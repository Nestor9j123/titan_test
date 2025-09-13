package nitchcorp.backend.titan.shared.securite.user.controllers;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.shared.securite.user.dtos.request.LoginRequest;
import nitchcorp.backend.titan.shared.securite.user.dtos.request.UserRequest;
import nitchcorp.backend.titan.shared.securite.user.dtos.response.UserAuthenticationResponse;
import nitchcorp.backend.titan.shared.securite.user.dtos.response.UserResponse;
import nitchcorp.backend.titan.shared.securite.user.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Tag(name = "UserController", description = "API pour UserController")
@RestController
@RequestMapping("api/user")
@Slf4j
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Create a new user", description = "Creates a new user (ADMIN, USER) and sends email if necessary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        try {
            UserResponse response = userService.createUser(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'utilisateur", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns JWT token")
    public ResponseEntity<UserAuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            UserAuthenticationResponse response = userService.authenticate(loginRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Échec de l'authentification", e);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new UserAuthenticationResponse(
                            null, null, null, null,
                            null, null, null, null,
                            null, false
                    ));
        }
    }

    @PatchMapping("/etat/{trackingId}")
    @Operation(summary = "Update user activation status", description = "Activates or deactivates an ADMIN user (done by ADMIN_TITAN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error or email sending failed")
    })
    public ResponseEntity<UserResponse> updateUserEtat(
            @RequestParam boolean etat,
            @PathVariable UUID trackingId) {

        try {
            UserResponse response = userService.updateUserEtat(trackingId, etat);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.error("Utilisateur non trouvé: {}", e.getMessage());
                return ResponseEntity.notFound().build();
            } else {
                log.error("Erreur lors de la mise à jour de l'état de l'utilisateur: {}", e.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de l'état de l'utilisateur", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
