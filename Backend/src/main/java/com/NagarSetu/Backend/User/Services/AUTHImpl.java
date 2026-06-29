package com.NagarSetu.Backend.User.Services;


import com.NagarSetu.Backend.City.CityRepository;
import com.NagarSetu.Backend.CompleteSecurity.JwtService;
import com.NagarSetu.Backend.CompleteSecurity.RefreshTokenRepository;
import com.NagarSetu.Backend.CompleteSecurity.Security.AuthUtil;
import com.NagarSetu.Backend.CompleteSecurity.Security.CookieService;
import com.NagarSetu.Backend.Entities.RefreshToken;
import com.NagarSetu.Backend.Entities.User;
import com.NagarSetu.Backend.Entities.UserRole;
import com.NagarSetu.Backend.Entities.UserStatus;
import com.NagarSetu.Backend.User.DTOs.*;
import com.NagarSetu.Backend.User.UserRepository.UserRepository;
import com.NagarSetu.Backend.Ward.WardRepository;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.NagarSetu.Backend.Entities.RefreshToken;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AUTHImpl implements AUTH {
    private final WardRepository wardRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final ObjectMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthUtil authUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;


    @Override
    public UserLoginResponseDTO login(UserLoginRequestDTO request, HttpServletResponse response) {

        if(request.getPhone() == null || request.getPhone().isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if(request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhone(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new RuntimeException("Your account has been blocked.");
        }

        String jti = UUID.randomUUID().toString();
        String accessToken = authUtil.generateAccessToken(user);

        String refreshToken = authUtil.generateRefreshToken(user,jti);


        RefreshToken Refresh_Token = jwtService.createRefreshToken(user,jti);


//        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);
//
//        tokens.forEach(token -> token.setRevoked(true));
//
//        refreshTokenRepository.saveAll(tokens);



        cookieService.attachRefreshTokenToCookie(response,refreshToken,(int)authUtil.getRefreshTokenValidityInMillis());
        cookieService.addNoStoreHeader(response);

        return UserLoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(authUtil.getAccessTokenValidityInMillis())
                .id(user.getId())
                .build();

    }




    @Override
    public RefreshResponse refresh(RefreshRequest refreshRequest) {

        String refreshToken = refreshRequest.getRefreshToken();

        if(refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        if(!authUtil.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String jti = authUtil.getJti(refreshToken);
        UUID userId = authUtil.getUserId(refreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if(storedToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new RuntimeException("Refresh token has expired");
        }
        if(storedToken.isRevoked()) {
            jwtService.revokeAllUserTokens(storedToken.getUser());
            throw new RuntimeException("Refresh token has been revoked");
        }

        if(!storedToken.getUser().getId().equals(userId)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String jtii = UUID.randomUUID().toString();
        User user = storedToken.getUser();
        String newAccessToken = authUtil.generateAccessToken(user);
        String newRefreshToken = authUtil.generateRefreshToken(user,jtii);

        RefreshToken newrefreshToken = jwtService.rotateRefreshToken(storedToken,jtii);




        return RefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(authUtil.getAccessTokenValidityInMillis())
                .build();

    }



    @Override
    public RegisterResponseDTO signUp(RegisterRequestDTO request) {

        try {

            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new IllegalArgumentException("Password is required");
            }
            if (userRepository.findByPhone(request.getPhone()).isPresent()) {
                throw new RuntimeException("User already exists");
            }
            if (request.getLocation() == null) {
                throw new BadRequestException("Location is required");
            }
            String email = request.getEmail();


            if (email != null && email.isBlank()) {
                email = null;
            }


            String geoJson;
            try {
                geoJson = mapper.writeValueAsString(request.getLocation());
            } catch (Exception e) {
                throw new RuntimeException("Invalid GeoJSON");
            }

            Map<String, Object> result = userRepository.registerUser(
                    request.getPhone(),
                    passwordEncoder.encode(request.getPassword()),
                    request.getName(),
                    email,
                    geoJson
            );




            return RegisterResponseDTO.builder()
                    .id((UUID) result.get("id"))
                    .phone((String) result.get("phone"))
                    .name((String) result.get("name"))
                    .email((String) result.get("email"))
                    .wardId((UUID) result.get("ward_id"))
                    .cityId((UUID) result.get("city_id"))
                    .location(result.get("location") != null
                            ? mapper.readValue((String) result.get("location"), Map.class)
                            : null)
                    .role(UserRole.CITIZEN)
                    .build();



        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }


    @Override
    public void logout(LogoutRequestDTO request) {
String refreshToken = request.getRefreshToken();

        if(refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

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

        jwtService.revokeAllUserTokens(storedToken.getUser());

    }


    private Map<String, Object> parsePoint(String wkt) {
        if (wkt == null) return null;

        // POINT(lng lat)
        wkt = wkt.replace("POINT(", "").replace(")", "");
        String[] parts = wkt.split(" ");

        Map<String, Object> map = new HashMap<>();
        map.put("lng", Double.parseDouble(parts[0]));
        map.put("lat", Double.parseDouble(parts[1]));

        return map;
    }



}
