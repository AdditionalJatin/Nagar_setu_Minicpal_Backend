package com.NagarSetu.Backend.MasterAdmin.Services;


import com.NagarSetu.Backend.MasterAdmin.DTOs.RegisterWardDTO;
import com.NagarSetu.Backend.MasterAdmin.DTOs.RegisterWardResponseDTO;
import com.NagarSetu.Backend.MasterAdmin.DTOs.WardListResponseDTO;

import java.util.List;
import java.util.UUID;

public interface WardCRUD {
    RegisterWardResponseDTO createWard(RegisterWardDTO dto);

    RegisterWardResponseDTO updateWard(UUID wardId, RegisterWardDTO dto);

    void deleteWard(UUID wardId);

    List<WardListResponseDTO> getWardsByCity(UUID cityId);
}
