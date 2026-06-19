package com.NagarSetu.Backend.MasterAdmin.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityListTResponseDTO {
    private UUID id;
    private String name;
    private String state;
    private Long population;
    private Double areaSqKm;
}
