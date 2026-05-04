package sia.sever.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import sia.sever.exception.JwtExpiredException;
import sia.sever.exception.JwtInvalidException;
import sia.sever.security.userDetails.CustomUserDetailsService;
import java.io.IOException;

// Runs on EVERY request to check JWT authentication
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Fields
    // Service to load user from DB
    private final CustomUserDetailsService userService;
    // Handles JWT creation + parsing
    private final JwtUtility jwtUtility;

    // Constructor
    public JwtAuthenticationFilter(CustomUserDetailsService userService, JwtUtility jwtUtility) {
        this.userService = userService;
        this.jwtUtility = jwtUtility;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Get Authorization header from request
        String authHeader = request.getHeader("Authorization");

        // Check if Bearer token exists
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // try to parse token
            try {
                // Remove "Bearer " prefix and extract token
                String token = authHeader.substring(7);
                // Extract email (subject) from JWT
                String email = jwtUtility.extractEmail(token);

                // If user not already authenticated in this request
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Load user from database
                    UserDetails userDetails = userService.loadUserByUsername(email);
                    // Set authenticated user in Spring Security context
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                    );
                }
            } catch (JwtExpiredException e) {
                SecurityContextHolder.clearContext();
                response.sendError(401, "JWT token is expired, please try again");
                return;
            } catch (JwtInvalidException e) {
                SecurityContextHolder.clearContext();
                response.sendError(401, "JWT token is invalid, please try again");
                return;
            }catch (Exception e){
                SecurityContextHolder.clearContext();
                response.sendError(500, "Something went wrong, please try again");
                return;
            }
        }
        // Continue request if everything is fine
        chain.doFilter(request, response);
    }
}