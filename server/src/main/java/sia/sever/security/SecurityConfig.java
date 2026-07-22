package sia.sever.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import sia.sever.security.jwt.JwtAuthenticationFilter;
import sia.sever.security.jwt.JwtUtility;
import sia.sever.security.userDetails.CustomUserDetailsService;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userService;
    private final JwtUtility jwtUtility;

    public SecurityConfig(CustomUserDetailsService userService, JwtUtility jwtUtility){
        this.userService = userService;
        this.jwtUtility = jwtUtility;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Disable CSRF (stateless API)
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no auth needed)
                        .requestMatchers("/api/users/auth/login", "/api/users/auth/register").permitAll()
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                // No sessions (JWT = stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Add JWT filter before Spring's authentication filter
                .addFilterBefore(new JwtAuthenticationFilter(userService, jwtUtility), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Trusted frontend URL to
                                                                              // accept requests
        // For get, post, put etc.
        configuration.setAllowedMethods(List.of("POST", "GET", "PUT", "DELETE", "OPTIONS"));
        // These HTTP methods are allowed. Options is a preflight request your browser sends before the
        // actual POST. Without it, login still wouldn't work.

        configuration.setAllowedHeaders(List.of("*")); // Accept any request headers.Later, when you're
                                                          // sending JWTs, your browser will include:
                                                          // Authorization: Bearer eyJhbGci...
                                                          // This line allows that.

        // This creates the object that Spring Security is expecting.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Use these CORS rules for every
                                                                       // endpoint.
        return source;
    }
}
