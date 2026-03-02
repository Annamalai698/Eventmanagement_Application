package com.example.eventmanagement_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final String TAG = "JwtUtils Error: ";

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // Helper method to generate a secure Key from the Base64 secret string
    private Key getSigningKey() {
        // Decode the Base64 string from application.properties
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        // Create a secure Key object suitable for HS512
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl)authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getEmail()) // explicitly use email
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getEmailFromJwtToken(String token) {
        // Use the new getSigningKey() method for parsing
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            // Use the new getSigningKey() method for validation
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            System.err.println(TAG + "Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println(TAG + "Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println(TAG + "JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println(TAG + "JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println(TAG + "JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


}
