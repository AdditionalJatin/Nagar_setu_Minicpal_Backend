package com.NagarSetu.Backend.CityAdmin.DTOs;


import lombok.Data;

import java.util.UUID;

@Data
public class CityPeopleRegisterDTO {
    private String name;
    private String phone;
    private String password;
    private String email;
    private UUID cityId;
    private UUID wardId;
    private String role;        // DEPARTMENT_ADMIN / WARD_HEAD
    private String department;  // required for department admin

}
