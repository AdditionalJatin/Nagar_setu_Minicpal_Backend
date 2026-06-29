package com.NagarSetu.Backend.User.DTOs;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponseDTO {
    private UUID id;

    private String accessToken;

    private String refreshToken;

    private Long expiresIn;
}
