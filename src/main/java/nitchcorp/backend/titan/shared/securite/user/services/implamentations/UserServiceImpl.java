package nitchcorp.backend.titan.shared.securite.user.services.implamentations;

import nitchcorp.backend.titan.shared.securite.config.TitanEmailConstants;
import nitchcorp.backend.titan.shared.securite.mailling.dto.request.EmailRequest;
import nitchcorp.backend.titan.shared.securite.mailling.service.EmailService;
import nitchcorp.backend.titan.shared.securite.securiteconfig.UserDetailsconf.UserDetailsImpl;
import nitchcorp.backend.titan.shared.securite.securiteconfig.configSecurity.RandomGenerator;
import nitchcorp.backend.titan.shared.securite.securiteconfig.jwt.JwtUtils;
import nitchcorp.backend.titan.shared.securite.user.Mapper.UserMapper;
import nitchcorp.backend.titan.shared.securite.user.dtos.request.LoginRequest;
import nitchcorp.backend.titan.shared.securite.user.dtos.request.UserRequest;
import nitchcorp.backend.titan.shared.securite.user.dtos.response.UserAuthenticationResponse;
import nitchcorp.backend.titan.shared.securite.user.dtos.response.UserResponse;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.securite.user.repositories.UserRepository;
import nitchcorp.backend.titan.shared.securite.user.services.UserService;
import nitchcorp.backend.titan.shared.utils.constantSecurities.PasswordGenerator;
import nitchcorp.backend.titan.shared.utils.constantSecurities.PasswordResult;
import nitchcorp.backend.titan.shared.utils.constantSecurities.TypeRole;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final RandomGenerator randomGenerator;

    public UserServiceImpl(UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, UserMapper userMapper, RandomGenerator randomGenerator) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.userMapper = userMapper;
        this.randomGenerator = randomGenerator;
    }

    @Override
    public UserAuthenticationResponse authenticate(LoginRequest loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.email(),
                            loginDTO.password())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<String> rolesList = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            return new UserAuthenticationResponse(
                    token,
                    userDetails.getId(),
                    userDetails.getFirstName(),
                    userDetails.getLastName(),
                    userDetails.getPhone(),
                    userDetails.getEmail(),
                    userDetails.getRoles(),
                    rolesList,
                    userDetails.getTrackingId(),
                    userDetails.isActif()
            );

        } catch (BadCredentialsException ex) {
            throw new IllegalArgumentException("Les paramètres de connexion sont incorrects");
        }
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        User user = new User();

        // Mapping des champs
        user.setTrackingId(UUID.randomUUID());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setEmail(request.email());

        TypeRole role = TypeRole.valueOf(request.role().toUpperCase());
        user.setRole(role);

        // Définir l'état initial
        if (role == TypeRole.ADMIN) {
            user.setActif(false); // Admin doit être validé par ADMIN_TITAN
        } else {
            user.setActif(true);  // USER et ADMIN_TITAN actifs immédiatement
        }

        // Encoder le mot de passe fourni par l'utilisateur
        user.setPassword(passwordEncoder.encode(request.password()));

        // Sauvegarder l'utilisateur
        user = userRepository.save(user);

        // Envoi email uniquement si Admin (demande de validation)
        if (role == TypeRole.ADMIN) {
            EmailRequest emailRequest = buildDefaultEmailRequest(user, request.password());
            emailService.send(emailRequest, TitanEmailConstants.SUBSCRIPTION_TEMPLATE);
        }

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUserEtat(UUID trackingId, boolean etat) {
        User user = userRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("User not found with trackingId: " + trackingId));

        user.setActif(etat);
        user = userRepository.save(user);

        // Si c'est un ADMIN activé par ADMIN_TITAN
        if (user.getRole() == TypeRole.ADMIN && etat) {
            PasswordResult passwordResult = PasswordGenerator.generateSecurePassword();
            user.setPassword(passwordEncoder.encode(passwordResult.hashedPassword()));
            userRepository.save(user);

            EmailRequest emailRequest = buildDefaultEmailRequest(user, passwordResult.plainPassword());
            emailService.send(emailRequest, TitanEmailConstants.EMAIL_TEMPLATE_ACCOUNT_VALIDATION);
        }

        return userMapper.toResponse(user);
    }


    private EmailRequest buildDefaultEmailRequest(User user, String password) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return EmailRequest.builder()
                .mailFrom(TitanEmailConstants.EMAIL_FROM)
                .mailTo(user.getEmail())
                .mailCc(TitanEmailConstants.EMAIL_CC)
                .mailBcc(TitanEmailConstants.EMAIL_BCC)
                .mailSubject(TitanEmailConstants.EMAIL_SUBJECT)
                .mailContent(TitanEmailConstants.EMAIL_CONTENT)
                .entreprise(TitanEmailConstants.COMPANY_NAME)
                .contact(TitanEmailConstants.SUPPORT_EMAIL)
                .nom(user.getFirstName() + " " + user.getLastName())
                .username(user.getEmail())
                .password(password)
                .lien(TitanEmailConstants.CONFIRMATION_BASE_URL + user.getTrackingId())
                .build();
    }

    private void sendSubscriptionEmail(EmailRequest emailRequest, String template) {
        try {
            emailService.send(emailRequest, template);
        } catch (Exception e) {
            String errorMessage = "Échec de l'envoi de l'email de confirmation: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }
}
