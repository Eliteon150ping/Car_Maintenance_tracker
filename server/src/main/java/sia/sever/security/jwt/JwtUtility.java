package sia.sever.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sia.sever.exception.JwtExpiredException;
import sia.sever.exception.JwtInvalidException;
import java.security.Key;
import java.util.Date;
import static io.jsonwebtoken.Jwts.parserBuilder;

@Component
public class JwtUtility {

    // Fields
    @Value(value = "${jwt.secret}")
    private String secretKey;

    @Value(value = "${jwt.expiration}")
    private Long expirationTime;

    // Get the secret key
    private Key signingKey;
    @PostConstruct
    private void init(){
        signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }


    // Generate Token
    public String generateToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(email)         // Set subject (email or username)
                .claim("userId", userId)  // Create claims (optional) like roles, user ids, permissions etc.
                .setIssuedAt(new Date())   // Set issued time (now)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Set expiration time
                .signWith(signingKey, SignatureAlgorithm.HS256)  // Sign token with secret key
                .compact();
    }

    // Parse token
    private Claims parseToken(String token){
        try{
            return parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e){
            throw new JwtExpiredException("JWT token is expired, please re-login again");
        }catch (JwtException e) {
            throw new JwtInvalidException("JWT token is invalid, please try again");
        }
    }

    // Extract Email from Token
    public String extractEmail(String token){
        Claims claims = parseToken(token);
        return claims.getSubject(); // Return subject
    }

    public Long extractUserId(String token){
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }
}
