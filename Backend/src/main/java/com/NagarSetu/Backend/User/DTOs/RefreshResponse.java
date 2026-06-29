package com.NagarSetu.Backend.User.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshResponse {
    private String accessToken;
    private  String refreshToken;

    private Long expiresIn;
}
