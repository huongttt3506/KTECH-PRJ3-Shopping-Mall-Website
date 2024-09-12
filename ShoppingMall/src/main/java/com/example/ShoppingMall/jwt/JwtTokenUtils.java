package com.example.ShoppingMall.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.example.ShoppingMall.user.entity.UserEntity;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

//  this class is responsible for generating JWT tokens for user authentication,
//  validating those tokens, and parsing token claims.

@Slf4j
@Component
public class JwtTokenUtils {
    // A cryptographic key used to sign JWT tokens.
    private final Key secretKey;
    // A parser used to decode and validate JWT tokens.
    private final JwtParser jwtParser;

    public JwtTokenUtils(
            @Value("${jwt.secret}")
            String jwtSecret
    ) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(this.secretKey)
                .build();
    }
    // This method generates a JWT token for a given UserEntity
    public String generateToken(UserEntity userEntity) {
        // current time : now
        Instant now = Instant.now();

        //Get role
        String role = userEntity.getRole().name();

        // payload info
        Claims jwtClaims = Jwts.claims()
                .setSubject(userEntity.getUsername()) // sub
                .setIssuedAt(Date.from(now)) // iat
                .setExpiration(Date.from(now.plusSeconds(60 * 60))); // exp
        // put("role", role);
        jwtClaims.put("role", role);

        String jwt = Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(this.secretKey)
                .compact();
        log.info("username: {}", userEntity.getUsername());
        log.info("role: {}", role);
        log.info("payload: {}", jwtClaims);
        log.info("token: {}", jwt);
        log.info("Claims username: {}", jwtClaims.getSubject());
        log.info("Claims issuedAt: {}", jwtClaims.getIssuedAt());
        log.info("Claims expiration: {}", jwtClaims.getExpiration());
        log.info("Claims role: {}", jwtClaims.get("role"));

        return jwt;
    }
    // This method checks if a given token is valid.
    public boolean validate(String token) {
        try {
            //jwtParser to attempt parsing the token.
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("invalid jwt");
        } return false;
    }

    //extracts the claims (information) from a valid JWT token.
    //to parse the token and returns the body (e.g., username, issued time, expiration).
    public Claims parseClaims(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

}