package io.chenyiax.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.chenyiax.configuration.JwtConfig;
import io.chenyiax.exception.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This utility class is used to create and parse JWT (JSON Web Token) tokens.
 * It leverages the Auth0 JWT library to handle token generation and verification.
 * The {@link Component} annotation marks this class as a Spring component,
 * allowing it to be automatically detected and managed by the Spring container.
 * The {@link RequiredArgsConstructor} annotation from Lombok generates a constructor
 * with required arguments for all final fields, simplifying dependency injection.
 */
@Component
@RequiredArgsConstructor
public class JwtUtils {
    private final JwtConfig jwtConfig;
    private final Clock clock;

    /**
     * Creates a JWT token based on the provided user information.
     * This method uses the HMAC256 algorithm to sign the token and includes user - related claims such as username and authorities.
     *
     * @param user The user details object containing the user's username and authorities.
     * @return A string representing the generated JWT token.
     */
    public String createToken(UserDetails user) {
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getKey());
        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(jwtConfig.getValidity(), ChronoUnit.SECONDS);

        return JWT.create()
                .withClaim("name", user.getUsername())
                .withClaim("authorities", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .withExpiresAt(Date.from(expiresAt))
                .withIssuedAt(Date.from(now))
                .sign(algorithm);
    }

    /**
     * Parses and verifies a JWT token.
     * This method checks the token's signature and expiration time. If the token is valid,
     * it extracts the user information from the claims and creates a UserDetails object.
     *
     * @param token The JWT token string to be parsed and verified.
     * @return A UserDetails object containing the user information extracted from the token.
     * @throws JwtException If the token is invalid or has expired.
     */
    public UserDetails parseToken(String token) throws JwtException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getKey());
            DecodedJWT jwt = JWT.require(algorithm)
                    .build()
                    .verify(token);
            Map<String, Claim> claims = jwt.getClaims();
            if (Instant.now(clock).isAfter(claims.get("exp").asDate().toInstant())) {
                throw new JwtException("Token expired");
            }

            return User.withUsername(claims.get("name").asString())
                    .password("")
                    .authorities(claims.get("authorities").asArray(String.class))
                    .build();
        } catch (JWTVerificationException e) {
            throw new JwtException("Invalid token" + e);
        }
    }
}