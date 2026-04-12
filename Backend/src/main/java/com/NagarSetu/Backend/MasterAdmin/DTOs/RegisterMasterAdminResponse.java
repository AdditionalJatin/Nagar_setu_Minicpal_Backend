package com.NagarSetu.Backend.MasterAdmin.DTOs;

import lombok.Data;

import java.util.UUID;

@Data
public class RegisterMasterAdminResponse {

    private String name;
    private String phone;
    private String role;
}
