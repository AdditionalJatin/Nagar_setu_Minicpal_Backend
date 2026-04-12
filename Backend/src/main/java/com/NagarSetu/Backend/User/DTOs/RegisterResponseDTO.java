package com.NagarSetu.Backend.User.DTOs;


import com.NagarSetu.Backend.Entities.UserRole;
import com.NagarSetu.Backend.Entities.UserStatus;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponseDTO {
    UUID id;
    private String phone;
    private String name;
    private String email;

    private UUID wardId;
    private UUID cityId;
    private UserRole role;
    private UserStatus status;
    private  Map<String, Object> location;


}
