package com.NagarSetu.Backend.User.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequestDTO {
    String phone;
    String password;
}
