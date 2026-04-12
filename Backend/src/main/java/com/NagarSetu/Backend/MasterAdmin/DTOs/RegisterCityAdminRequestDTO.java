package com.NagarSetu.Backend.MasterAdmin.DTOs;

import lombok.Data;

import java.util.UUID;

@Data
public class RegisterCityAdminRequestDTO {
    private String name;
    private String phone;
    private String password;
    private String email;

    private UUID cityId;
}
