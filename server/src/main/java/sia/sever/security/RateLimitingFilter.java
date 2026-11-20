package sia.sever.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Profile("!test")
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private static final String TRUSTED_PROXY_IP = "172.18.0.10";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain

    ) throws ServletException, IOException {

        String endpoint = request.getRequestURI();

        if (!"POST".equalsIgnoreCase(request.getMethod())
                || (!endpoint.equals("/api/users/auth/login")
                && !endpoint.equals("/api/users/auth/register"))) {

            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);

        Bucket bucket = resolveBucket(clientIp, endpoint);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\":\"Too many requests. Please try again later.\"}"
            );
        }
    }

    private Bucket createNewBucket(int capacity) {

        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(capacity, Duration.ofMinutes(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket resolveBucket(String clientIp, String endpoint) {

        String key = clientIp + ":" + endpoint;

        int capacity = endpoint.equals("/api/users/auth/login") ? 5 : 3;

        return buckets.computeIfAbsent(key, ignored -> createNewBucket(capacity));
    }

    private String getClientIp(HttpServletRequest request) {

        String remoteAddress = request.getRemoteAddr();

        // Only trust X-Real-IP when the request comes from our Nginx proxy.
        if (TRUSTED_PROXY_IP.equals(remoteAddress)) {

            String clientIp = request.getHeader("X-Real-IP");

            if (clientIp != null && !clientIp.isBlank()) {
                return clientIp;
            }
        }

        // Direct requests use their actual remote address.
        return remoteAddress;
    }

}
