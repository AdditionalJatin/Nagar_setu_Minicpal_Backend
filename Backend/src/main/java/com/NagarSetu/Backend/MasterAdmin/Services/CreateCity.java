package com.NagarSetu.Backend.MasterAdmin.Services;

import com.NagarSetu.Backend.Entities.City;
import com.NagarSetu.Backend.MasterAdmin.DTOs.CityListTResponseDTO;
import com.NagarSetu.Backend.MasterAdmin.DTOs.RegisterCItyResponseDTO;
import com.NagarSetu.Backend.MasterAdmin.DTOs.RegisterCityDTO;


import java.util.List;
import java.util.UUID;

public interface CreateCity {
    RegisterCItyResponseDTO createCity(RegisterCityDTO registerCityDTO);

    RegisterCItyResponseDTO updateCity(UUID cityId, RegisterCityDTO registerCityDTO);

    void deleteCity(UUID cityId);

    List<CityListTResponseDTO> getAllCities();

}
