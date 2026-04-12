package com.NagarSetu.Backend.MasterAdmin.DTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WardListResponseDTO {
    private UUID id;
    private String name;
    private Integer wardNumber;
    private Integer population;
    private Double areaSqKm;

}
