package com.NagarSetu.Backend.MasterAdmin.DTOs;


import lombok.Data;

@Data
public class RegisterCityAdminResponseDTO {
    private String name;
    private String phone;
    private String role;
    private String cityName;
}
