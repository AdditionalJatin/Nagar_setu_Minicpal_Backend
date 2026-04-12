package com.NagarSetu.Backend.MasterAdmin.DTOs;


import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotBlank;

import lombok.*;


import java.awt.*;
import java.util.Map;

@Data
public class RegisterCityDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String state;

    private String country;



 private Map<String, Object> geometry;

    private Long population;
    private Double areaSqKm;
}
