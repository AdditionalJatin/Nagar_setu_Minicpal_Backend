package com.NagarSetu.Backend.CompleteSecurity.Security;

import com.NagarSetu.Backend.Entities.RefreshToken;
import com.NagarSetu.Backend.Entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Getter
@Setter
public class AuthUtil {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${jwt.accessTokenValidityInMillis}")
    private  long accessTokenValidityInMillis;
    @Value("${jwt.refreshTokenValidityInMillis}")
    private  long refreshTokenValidityInMillis;
    @Value("${jwt.issuer}")
    private  String issuer;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecretKey)
        );
    }


    public String generateAccessToken(User user) {

        Instant now = Instant.now();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTokenValidityInMillis)))
                .claim("role", user.getRole().name())
                .claim("type", "access")
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(User user, String jti) {

        Instant now = Instant.now();

        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(refreshTokenValidityInMillis)))
                .claim("type", "refresh")
                .signWith(getSecretKey())
                .compact();
    }


    public Jws<Claims> parse(String token) throws ExpiredJwtException, MalformedJwtException, SecurityException, IllegalArgumentException {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);
    }




    public boolean validate(String token) {

        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }


    public Claims getClaims(String token) {
        return parse(token).getPayload();
    }

    public UUID getUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String getJti(String token) {
        return getClaims(token).getId();
    }

    public String getType(String token) {
        return getClaims(token).get("type", String.class);
    }

    public boolean isAccessToken(String token) {
        return "access".equals(getType(token));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(getType(token));
    }

    public Date getIssuedAt(String token) {
        return getClaims(token).getIssuedAt();
    }

    public Date getExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    public boolean isExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    public boolean isValidIssuer(String token) {
        return issuer.equals(getClaims(token).getIssuer());
    }



}
