package nitchcorp.backend.titan.shared.securite.securiteconfig.filters;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.shared.securite.securiteconfig.jwt.JwtUtils;
import nitchcorp.backend.titan.shared.utils.constantSecurities.JavaConstant;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;



import java.io.IOException;


import static nitchcorp.backend.titan.shared.utils.constantSecurities.JavaConstant.OPTIONS_HTTP_METHOD;
import static nitchcorp.backend.titan.shared.utils.constantSecurities.JwtConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;


@Component
@Slf4j
public class JwtAuthorizationToken extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthorizationToken(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException, MalformedJwtException, ExpiredJwtException {
        response.setHeader("Access-Control-Allow-Origin", JavaConstant. FRONTEND_URL);
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", String.valueOf(true));
        response.setHeader("Access-Control-Max-Age", String.valueOf(180));

        System.out.println("request "+request.getMethod());
        System.out.println("request uri "+request.getRequestURI());
        System.out.println("request token "+request.getHeader("Authorization"));
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(OK.value());
            System.out.println("filter 1");
        } else {
            System.out.println("filter 2");
            String authorizeHeader = request.getHeader(AUTHORIZATION);
            System.out.println("filter 3");
            if (authorizeHeader == null || !authorizeHeader.startsWith(TOKEN_PREFIX)) {
                System.out.println("filter 4");
                filterChain.doFilter(request, response);
                System.out.println("filter 5");
                return;
            }

            String token = authorizeHeader.substring(TOKEN_PREFIX.length());
            String username = jwtUtils.extractUsername(token);
            System.out.println("-----------------------------------------------"+token);
            //List<GrantedAuthority> authorities = jwtUtils.extractAuthorities(token);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            System.out.println("**************");
            System.out.println("--------------------- userDetails roles"+userDetails.getAuthorities());
            if (jwtUtils.isTokenValid(token, userDetails) && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("/**********************************************/");
                System.out.println("It works");
                System.out.println("/**********************************************/");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                System.out.println(authToken.isAuthenticated());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

}
