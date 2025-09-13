package nitchcorp.backend.titan.shared.securite.mailling.service.impl;




import jakarta.mail.internet.MimeMessage;
import nitchcorp.backend.titan.shared.securite.mailling.dto.request.EmailRequest;
import nitchcorp.backend.titan.shared.securite.mailling.dto.response.EmailResponse;
import nitchcorp.backend.titan.shared.securite.mailling.entity.EmailConfiguration;
import nitchcorp.backend.titan.shared.securite.mailling.mapper.EmailMapper;
import nitchcorp.backend.titan.shared.securite.mailling.repository.EmailRepository;
import nitchcorp.backend.titan.shared.securite.mailling.repository.EmailSendRepository;
import nitchcorp.backend.titan.shared.securite.mailling.service.EmailService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;



import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;


@Service
@Transactional
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;

    private final EmailSendRepository repository;

    private final SpringTemplateEngine templateEngine;

    private final EmailMapper emailMapper;

    public EmailServiceImpl(EmailRepository emailRepository, EmailSendRepository repository, SpringTemplateEngine templateEngine, EmailMapper emailMapper) {
        this.emailRepository = emailRepository;
        this.repository = repository;
        this.templateEngine = templateEngine;
        this.emailMapper = emailMapper;
    }

    @Override
    public EmailResponse send(EmailRequest request, String template) {
        return Optional.of(request)
                .stream()
                .peek(req -> sendMimeMessage(req, template))
                .map(emailMapper::toEntity)
                .peek(repository::save)
                .map(emailMapper::toDto)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Erreur lors de la sauvegarde"));
    }

    private boolean templateExists(String templateName) {
        Resource resource = new ClassPathResource("templates/" + templateName + ".html");
        return resource.exists();
    }
    public void sendMimeMessage(EmailRequest request, String template) {
        if (!templateExists(template)) {
            throw new IllegalArgumentException("Le template " + template + " n'existe pas.");
        }

        try {
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            JavaMailSender mailSender = getJavaMailSender();

            model.put("nom", request.getNom());
            model.put("plan", request.getPlan());
            model.put("username", request.getUsername());
            model.put("password", request.getPassword());
            model.put("startDate", request.getStartDate());
            model.put("lienCompte", request.getLien());
            model.put("endDate", request.getEndDate());
            model.put("montant", request.getMontant());
            model.put("contactEmail", request.getContact());

            context.setVariables(model);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            // Utilisation du moteur de template pour générer l'email
            String html = templateEngine.process(template, context);

            helper.setText(html, true);
            helper.setTo(request.getMailTo());
            helper.setSubject(request.getMailSubject());

            mailSender.send(message);

        } catch (jakarta.mail.MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JavaMailSender getJavaMailSender() {
        EmailConfiguration emailConfig = getEmailConfiguration();

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailConfig.getHost());
        mailSender.setPort(emailConfig.getPort());
        mailSender.setUsername(emailConfig.getUsername());
        mailSender.setPassword(emailConfig.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", emailConfig.isSmtpAuth());
        props.put("mail.smtp.starttls.enable", emailConfig.isStarttlsEnable());

        return mailSender;
    }

    public EmailConfiguration getEmailConfiguration() {
        return emailRepository.findFirstByOrderByIdAsc();
    }

}
