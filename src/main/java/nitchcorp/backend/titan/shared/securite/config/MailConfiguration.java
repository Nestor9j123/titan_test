package nitchcorp.backend.titan.shared.securite.config;


import nitchcorp.backend.titan.shared.securite.mailling.entity.EmailConfiguration;
import nitchcorp.backend.titan.shared.securite.mailling.repository.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfiguration {

    private final EmailRepository emailConfigurationRepository;
    private static final Logger logger = LoggerFactory.getLogger(MailConfiguration.class);

    public MailConfiguration(EmailRepository emailConfigurationRepository) {
        this.emailConfigurationRepository = emailConfigurationRepository;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.mail.host", matchIfMissing = false)
    public JavaMailSender javaMailSenderFromProperties() {
        logger.info("Utilisation de la configuration email depuis application.properties");
        return new JavaMailSenderImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.mail.host", havingValue = "", matchIfMissing = true)
    public JavaMailSender javaMailSenderFromDatabase() {
        logger.info("Utilisation de la configuration email depuis la base de données");

        EmailConfiguration config = emailConfigurationRepository.findAll()
                .stream()
                .findFirst()
                .orElse(getDefaultEmailConfiguration());

        logger.info("Configuration email - Host: {}, Port: {}, Username: {}",
                config.getHost(), config.getPort(), config.getUsername());
        logger.debug("Configuration email - Password: {}****", config.getPassword().substring(0, 4));

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPort());
        mailSender.setUsername(config.getUsername());
        mailSender.setPassword(config.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", config.isSmtpAuth());
        props.put("mail.smtp.starttls.enable", config.isStarttlsEnable());
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        props.put("mail.debug", "false");

        return mailSender;
    }

    private EmailConfiguration getDefaultEmailConfiguration() {
        logger.warn("Utilisation de la configuration email par défaut");
        EmailConfiguration defaultConfig = new EmailConfiguration();
        defaultConfig.setHost("smtp.gmail.com");
        defaultConfig.setPort(587);
        defaultConfig.setUsername("woroukoffijudeprudencio@gmail.com");  // Unifié
        defaultConfig.setPassword("xvnf dsts taau txzo");  // Nouveau mot de passe
        defaultConfig.setSmtpAuth(true);
        defaultConfig.setStarttlsEnable(true);
        return defaultConfig;
    }
}