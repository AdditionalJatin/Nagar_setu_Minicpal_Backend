package com.NagarSetu.Backend.MasterAdmin.DTOs;

import lombok.Data;

@Data
public class RegisterMasterAdminRequest {
    private String name;
    private String phone;
    private String password;
    private String email;
}
