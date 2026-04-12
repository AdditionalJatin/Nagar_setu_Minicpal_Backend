package com.NagarSetu.Backend.MasterAdmin.DTOs;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CityListTResponseDTO {
    private UUID id;
    private String name;
    private String state;
    private Long population;
    private Double areaSqKm;
}
