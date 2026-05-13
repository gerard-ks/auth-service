package dev.ks.authlayerarchitecture.service.auth;

import dev.ks.authlayerarchitecture.cache.RefreshTokenData;
import dev.ks.authlayerarchitecture.cache.RefreshTokenStore;
import dev.ks.authlayerarchitecture.exception.token.InvalidRefreshTokenException;
import dev.ks.authlayerarchitecture.exception.token.RefreshTokenExpiredException;
import dev.ks.authlayerarchitecture.security.jwt.JwtTokenProvider;
import dev.ks.authlayerarchitecture.security.jwt.TokenPair;
import dev.ks.authlayerarchitecture.security.web.TokenStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore;

    public TokenServiceImpl(JwtTokenProvider jwtTokenProvider, RefreshTokenStore refreshTokenStore) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenStore = refreshTokenStore;
    }


    @Override
    public TokenPair generatePair(UUID accountId,
                                  String email,
                                  List<String> roles,
                                  String strategy) {
        // Générer access token
        String accessToken = jwtTokenProvider.generateAccessToken(
                accountId, email, roles
        );

        // Générer refresh token
        UUID tokenId = UUID.randomUUID();

//        // Résoudre la stratégie
//        TokenResponseStrategy strategy =
//                tokenStrategyFactory.resolve(request);

        // Sauvegarder le refresh token
        refreshTokenStore.save(
                tokenId,
                accountId,
                email,
               strategy
        );

        long expiresIn = jwtTokenProvider.getExpiresInSeconds();

        return new TokenPair(
                accessToken,
                tokenId.toString(),
                jwtTokenProvider.getExpiresInSeconds()
        );
    }

    @Override
    public TokenPair refresh(String refreshTokenStr) {
        UUID tokenId = parseTokenId(refreshTokenStr);

        RefreshTokenData data = refreshTokenStore
                .findById(tokenId)
                .orElseThrow(InvalidRefreshTokenException::new);

        // Vérifier révocation
        if (data.revoked()) {
            // Replay détecté → revokeAll (SEC-05)
            log.warn(
                    "Refresh token replay detected [accountId={}]",
                    data.accountId()
            );
            refreshTokenStore.revokeAll(data.accountId());
            throw new InvalidRefreshTokenException();
        }

        // Vérifier expiration
        if (Instant.parse(data.expiresAt()).isBefore(Instant.now())) {
            refreshTokenStore.revoke(tokenId);
            throw new RefreshTokenExpiredException();
        }

        // Rotation : révoquer l'ancien
        refreshTokenStore.revoke(tokenId);

        // Générer nouvelle paire
        UUID newTokenId = UUID.randomUUID();

        String newAccessToken = jwtTokenProvider.generateAccessToken(
                data.accountId(),
                data.email(),
                List.of()
        );

//        TokenResponseStrategy strategy =
//                tokenStrategyFactory.resolve(request);

        refreshTokenStore.save(
                newTokenId,
                data.accountId(),
                data.email(),
                data.strategy()
        );

        long expiresIn = jwtTokenProvider.getExpiresInSeconds();

        return new TokenPair(
                newAccessToken,
                newTokenId.toString(),
                expiresIn
        );
    }

    @Override
    public void revoke(String refreshTokenStr) {
        UUID tokenId = parseTokenId(refreshTokenStr);
        refreshTokenStore.revoke(tokenId);
    }

    @Override
    public void revokeAll(UUID accountId) {
        refreshTokenStore.revokeAll(accountId);
    }

    private UUID parseTokenId(String refreshTokenStr) {
        try {
            return UUID.fromString(refreshTokenStr);
        } catch (IllegalArgumentException ex) {
            throw new InvalidRefreshTokenException();
        }
    }
}
