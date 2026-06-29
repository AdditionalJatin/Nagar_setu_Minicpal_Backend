package com.NagarSetu.Backend.CompleteSecurity;


import com.NagarSetu.Backend.CompleteSecurity.Security.AuthUtil;
import com.NagarSetu.Backend.Entities.RefreshToken;
import com.NagarSetu.Backend.Entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceimpl implements JwtService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUtil authUtil;


    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user, String jti) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .jti(jti) // Secure, unique identifier
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(authUtil.getRefreshTokenValidityInMillis()))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Override
    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken,String jti) {
        // 1. Mark old token as revoked
        oldToken.setRevoked(true);

        // 2. Create new token
        RefreshToken newToken = createRefreshToken(oldToken.getUser(),jti);

        // 3. Link them for tracking/audit
        oldToken.setReplacedByToken(newToken.getJti());
        refreshTokenRepository.save(oldToken);

        return newToken;
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        // Find all active tokens for this user and mark them revoked
        // You will need to add a custom query in your repository for this
        refreshTokenRepository.revokeAllByUser(user.getId());
    }

    @Transactional
    public void revokeToken(String refreshToken) {
        if(!authUtil.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String jti = authUtil.getJti(refreshToken);
        UUID userId = authUtil.getUserId(refreshToken);


        RefreshToken storedToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if(!storedToken.getUser().getId().equals(userId)) {
            throw new RuntimeException("Invalid refresh token");
        }


        // If it's already revoked, we don't need to do anything
        if (!storedToken.isRevoked()) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
        }
    }



}