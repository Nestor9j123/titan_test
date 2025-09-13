package nitchcorp.backend.titan.shared.securite.securiteconfig.configSecurity;

import nitchcorp.backend.titan.shared.securite.securiteconfig.UserDetailsconf.UserServicesSecure;
import nitchcorp.backend.titan.shared.securite.securiteconfig.jwt.AuthEntryPointJwt;
import nitchcorp.backend.titan.shared.securite.securiteconfig.jwt.AuthTokenFilter;
import nitchcorp.backend.titan.shared.utils.constantSecurities.JavaConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  // Optionnel si tu n'utilises plus @PreAuthorize, tu peux le retirer
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authenticationFilter;
    private final UserServicesSecure userServicesSecure;

    public SecurityConfig(AuthEntryPointJwt unauthorizedHandler,
                          AuthTokenFilter authenticationFilter,
                          @Lazy UserServicesSecure userServicesSecure) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.authenticationFilter = authenticationFilter;
        this.userServicesSecure = userServicesSecure;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // URLs publiques (sans auth)
                        .requestMatchers(JavaConstant.PUBLIC_URLS).permitAll()
                        // URLs pour utilisateurs authentifiés (ROLE_USER ou supérieur)
                        .requestMatchers(JavaConstant.AUTHENTICATED_URLS).hasAnyRole("USER", "ADMIN", "TITAN_ADMIN")
                        .requestMatchers(JavaConstant.ADMIN_URLS).hasAnyRole("ADMIN", "TITAN_ADMIN")
                        .requestMatchers(JavaConstant.TITAN_ADMIN_URLS).hasRole("TITAN_ADMIN")
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Les autres beans restent inchangés (passwordEncoder, authenticationProvider, authenticationManager, corsConfigurationSource)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userServicesSecure);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}