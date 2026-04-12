package com.NagarSetu.Backend.CityAdmin.Services;


import com.NagarSetu.Backend.CityAdmin.DTOs.CityPeopleRegisterDTO;
import com.NagarSetu.Backend.CityAdmin.DTOs.CityPeopleRegisterResponseDTO;

public interface RegisterPeople {
    CityPeopleRegisterResponseDTO registerPeople(CityPeopleRegisterDTO request);

}
