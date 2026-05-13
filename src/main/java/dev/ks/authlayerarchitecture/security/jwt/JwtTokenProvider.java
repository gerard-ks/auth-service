package dev.ks.authlayerarchitecture.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(
            UUID accountId,
            String email,
            List<String> roles
    ) {
        try {
            Instant now       = Instant.now();
            Instant expiresAt = now.plusSeconds(
                    jwtProperties.ttlMinutes() * 60
            );

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .jwtID(UUID.randomUUID().toString())
                    .issuer(jwtProperties.issuer())
                    .subject(accountId.toString())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiresAt))
                    .claim("email", email)
                    .claim("roles", roles)
                    .build();

            JWSSigner signer = new MACSigner(
                    jwtProperties.secretKey().getBytes()
            );

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claims
            );

            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (JOSEException ex) {
            log.error("Failed to generate access token [accountId={}]",
                    accountId);
            throw new IllegalStateException(
                    "Failed to generate access token", ex
            );
        }
    }

    public long getExpiresInSeconds() {
        return jwtProperties.ttlMinutes() * 60;
    }
}
