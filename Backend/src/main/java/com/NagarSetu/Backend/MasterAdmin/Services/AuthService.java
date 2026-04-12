package com.NagarSetu.Backend.MasterAdmin.Services;

import com.NagarSetu.Backend.Entities.User;
import com.NagarSetu.Backend.Entities.UserRole;
import com.NagarSetu.Backend.MasterAdmin.DTOs.*;

import java.util.UUID;



public interface AuthService {
    RegisterMasterAdminResponse registerMasterAdmin(RegisterMasterAdminRequest userDTO);
    void deleteMasterAdmin(UUID userId);

    RegisterCityAdminResponseDTO registerCityAdmin(RegisterCityAdminRequestDTO cityAdminDTO);

    void deleteCityAdmin(UUID userId);

}
