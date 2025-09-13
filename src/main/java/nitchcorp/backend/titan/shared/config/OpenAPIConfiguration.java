package nitchcorp.backend.titan.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    @Primary
    public OpenAPI customOpenAPI() {
        // Configuration des serveurs
        Server devServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Serveur de développement - Profil: " + activeProfile);
        
        Server prodServer = new Server()
                .url("https://api.titan.nitchcorp.com")
                .description("Serveur de production");

        // Configuration du contact
        Contact contact = new Contact()
                .name("NITCH Corp")
                .email("dev@nitchcorp.com")
                .url("https://nitchcorp.com");

        // Configuration de la licence
        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Titan Backend API")
                .version("1.0.0")
                .contact(contact)
                .license(license)
                .description("API REST pour l'application Titan - Plateforme complète incluant la livraison, événements, restauration, immobilier et gestion de contenu. Développé avec Spring Boot 3 et Spring Modulith.");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token pour l'authentification")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
