package com.NagarSetu.Backend.CompleteSecurity;

import com.NagarSetu.Backend.Entities.RefreshToken;
import com.NagarSetu.Backend.Entities.User;

public interface JwtService {

    RefreshToken createRefreshToken(User user,String jti);

    RefreshToken verifyExpiration(RefreshToken token);

    RefreshToken rotateRefreshToken(RefreshToken oldToken,String jti);

    void revokeAllUserTokens(User user);

    void revokeToken(String jti);
}
