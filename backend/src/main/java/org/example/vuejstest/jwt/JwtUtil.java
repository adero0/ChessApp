package org.example.vuejstest.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private Key secret = Keys.hmacShaKeyFor("your_secret_key_at_least_32_chars_long".getBytes());
    private int expiration = 86400000/2; // 1 day in milliseconds

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .issuedAt(new Date())
                .subject(userDetails.getUsername())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secret)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}
