package com.NagarSetu.Backend.User.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutRequestDTO {
   private String refreshToken;
}
