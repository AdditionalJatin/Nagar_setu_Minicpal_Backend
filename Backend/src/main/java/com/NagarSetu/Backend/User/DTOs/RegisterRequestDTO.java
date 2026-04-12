package com.NagarSetu.Backend.User.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {
    private String name;
    private String phone;
    private String password;
    private String email;
    private String cityName;
    private String wardName;

    private Object location;

}
