package com.NagarSetu.Backend.CityAdmin.Controller;


import com.NagarSetu.Backend.CityAdmin.DTOs.CityPeopleRegisterDTO;
import com.NagarSetu.Backend.CityAdmin.DTOs.CityPeopleRegisterResponseDTO;
import com.NagarSetu.Backend.CityAdmin.Services.RegisterPeople;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/city-admin")
@RequiredArgsConstructor
public class CityAdminController {
private final RegisterPeople registerPeople;

    @PostMapping("/register-people")
    public ResponseEntity<CityPeopleRegisterResponseDTO> signUp   (@RequestBody CityPeopleRegisterDTO user){
        CityPeopleRegisterResponseDTO response = registerPeople.registerPeople(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
