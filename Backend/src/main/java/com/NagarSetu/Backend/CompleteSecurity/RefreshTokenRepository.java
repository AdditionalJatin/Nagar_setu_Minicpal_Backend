package com.NagarSetu.Backend.CompleteSecurity;

import com.NagarSetu.Backend.Entities.RefreshToken;
import com.NagarSetu.Backend.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {


    Optional<RefreshToken> findByJti(String jti);


    List<RefreshToken> findAllByUser(User user);

    void deleteAllByUser(User user);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < :now OR r.revoked = true")
    int deleteAllExpiredOrRevokedTokens(Instant now);

    // 3. THE FIX: Used by RefreshTokenService to revoke all active tokens for a user
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId AND r.revoked = false")
    void revokeAllByUser(UUID userId);

}
