package com.assignment3.project.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.assignment3.project.entities.User;
import com.assignment3.project.repositories.UserRepository;
import com.assignment3.project.security.JwtConfig;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@AllArgsConstructor
public class TokenService {

    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;

    public String generateToken(Authentication authentication) {
        var header = new JWSHeader.Builder(jwtConfig.getAlgorithm())
                .type(JOSEObjectType.JWT)
                .build();

        Instant now = Instant.now();
        // Principal is UserDetails (Spring Security User), not our User entity
        // Get email from principal and load actual User entity from database
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        var claims = new JWTClaimsSet.Builder()
                .issuer("Codewiz")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(1, java.time.temporal.ChronoUnit.HOURS)))
                .claim("email", user.getEmail())
                .claim("id", user.getId())
                .claim("roles", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .build();

        var jwt = new SignedJWT(header, claims);

        try {
            var signer = new MACSigner(jwtConfig.getSecretKey());
            jwt.sign(signer);
        } catch (JOSEException e) {
            throw new RuntimeException("Error generating JWT", e);
        }

        return jwt.serialize();
    }
}