package nitchcorp.backend.titan.shared.securite.config;


import nitchcorp.backend.titan.shared.securite.mailling.entity.EmailConfiguration;
import nitchcorp.backend.titan.shared.securite.mailling.repository.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.UUID;

@Configuration
public class EmailConfigInitializer {

    private final EmailRepository emailConfigurationRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmailConfigInitializer.class);

    public EmailConfigInitializer(EmailRepository emailConfigurationRepository) {
        this.emailConfigurationRepository = emailConfigurationRepository;
    }

    @Bean
    public CommandLineRunner initEmailConfig() {
        return args -> {
            try {
                if (emailConfigurationRepository.count() == 0) {
                    logger.info("Initialisation de la configuration email...");
                    EmailConfiguration emailConfig = new EmailConfiguration();
                    emailConfig.setTrackingId(UUID.randomUUID());
                    emailConfig.setHost("smtp.gmail.com");
                    emailConfig.setPort(587);
                    emailConfig.setUsername("woroukoffijudeprudencio@gmail.com");  // Unifié
                    emailConfig.setPassword("xvnf dsts taau txzo");  // Nouveau mot de passe
                    emailConfig.setSmtpAuth(true);
                    emailConfig.setStarttlsEnable(true);

                    emailConfigurationRepository.save(emailConfig);
                    logger.info("Configuration email initialisée avec succès pour: {}", emailConfig.getUsername());
                } else {
                    // Mise à jour du mot de passe existant
                    logger.info("Configuration email existante trouvée, mise à jour du mot de passe...");
                    EmailConfiguration existingConfig = emailConfigurationRepository.findAll().stream().findFirst().orElse(null);
                    if (existingConfig != null) {
                        existingConfig.setUsername("woroukoffijudeprudencio@gmail.com");  // Unifié
                        existingConfig.setPassword("xvnf dsts taau txzo");  // Nouveau mot de passe
                        existingConfig.setHost("smtp.gmail.com");
                        existingConfig.setPort(587);
                        existingConfig.setSmtpAuth(true);
                        existingConfig.setStarttlsEnable(true);
                        emailConfigurationRepository.save(existingConfig);
                        logger.info("Configuration email mise à jour pour: {}", existingConfig.getUsername());
                    }
                }
            } catch (Exception e) {
                logger.error("Erreur lors de l'initialisation de la configuration email", e);
                throw e;
            }
        };
    }
}

