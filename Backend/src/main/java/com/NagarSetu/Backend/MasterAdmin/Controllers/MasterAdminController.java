package com.NagarSetu.Backend.MasterAdmin.Controllers;


import com.NagarSetu.Backend.MasterAdmin.DTOs.*;
import com.NagarSetu.Backend.MasterAdmin.Services.AuthService;
import com.NagarSetu.Backend.MasterAdmin.Services.CreateCity;
import com.NagarSetu.Backend.MasterAdmin.Services.WardCRUD;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/master-admin/")
@RequiredArgsConstructor
public class MasterAdminController {
    private final AuthService authService;
    private final CreateCity registerCity;
    private final WardCRUD wardCRUD;

    @PostMapping("/register-master-admin")
    public ResponseEntity<RegisterMasterAdminResponse> registerMasterAdmin(@RequestBody RegisterMasterAdminRequest userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerMasterAdmin(userDTO));

    }
    @PostMapping("/register-city-admin")
    public ResponseEntity<RegisterCityAdminResponseDTO> registerCityAdmin(@RequestBody RegisterCityAdminRequestDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerCityAdmin(userDTO));

    }


    @PostMapping("/register-city")
    public ResponseEntity<RegisterCItyResponseDTO> registerCity(@RequestBody RegisterCityDTO registerCityDTO) {
        RegisterCItyResponseDTO response = registerCity.createCity(registerCityDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/update-city/{cityId}")
    public ResponseEntity<RegisterCItyResponseDTO> updateCity(@PathVariable String cityId, @RequestBody RegisterCityDTO registerCityDTO) {
        RegisterCItyResponseDTO response = registerCity.updateCity(java.util.UUID.fromString(cityId), registerCityDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @PostMapping("/register-ward")
    public ResponseEntity<RegisterWardResponseDTO> registerWard(@RequestBody RegisterWardDTO registerWard) {
        RegisterWardResponseDTO response = wardCRUD.createWard(registerWard);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/update-ward/{wardId}")
    public ResponseEntity<RegisterWardResponseDTO> updateWard(@PathVariable String wardId, @RequestBody RegisterWardDTO registerWard) {
        RegisterWardResponseDTO response = wardCRUD.updateWard(java.util.UUID.fromString(wardId), registerWard);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/delete-city/{cityId}")
    public ResponseEntity<?> deleteCity(@PathVariable String cityId) {
        registerCity.deleteCity(java.util.UUID.fromString(cityId));
        return ResponseEntity.ok("City deleted successfully");
    }


    @DeleteMapping("/delete-ward/{wardId}")
    public ResponseEntity<?> deleteWard(@PathVariable String wardId) {
        wardCRUD.deleteWard(java.util.UUID.fromString(wardId));
        return ResponseEntity.ok("Ward deleted successfully");
    }

    @DeleteMapping("/delete-master-admin/{adminId}")
    public ResponseEntity<?> deleteMasterAdmin(@PathVariable String adminId) {
        authService.deleteMasterAdmin(java.util.UUID.fromString(adminId));
        return ResponseEntity.ok("Master Admin deleted successfully");
    }
    @DeleteMapping("/delete-city-admin/{adminId}")
    public ResponseEntity<?> deleteCityAdmin(@PathVariable String adminId) {
        authService.deleteCityAdmin(java.util.UUID.fromString(adminId));
        return ResponseEntity.ok("Master Admin deleted successfully");
    }

    @GetMapping("/cities")
    public ResponseEntity<?> getAllCities() {
        return ResponseEntity.ok(registerCity.getAllCities());
    }

    @GetMapping("/wards/{cityId}")
    public ResponseEntity<?> getAllWardsByCity(@PathVariable String cityId) {
        return ResponseEntity.ok(wardCRUD.getWardsByCity(java.util.UUID.fromString(cityId)));
    }

}
