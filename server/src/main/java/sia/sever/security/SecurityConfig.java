package sia.sever.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sia.sever.security.jwt.JwtAuthenticationFilter;
import sia.sever.security.jwt.JwtUtility;
import sia.sever.security.userDetails.CustomUserDetailsService;

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
}
