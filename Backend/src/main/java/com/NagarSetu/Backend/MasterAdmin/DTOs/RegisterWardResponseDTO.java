package com.NagarSetu.Backend.MasterAdmin.DTOs;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class RegisterWardResponseDTO {

    private UUID id;
    private String name;
    private UUID cityId;
    private Integer wardNumber;
    private Long population;
    private Double areaSqKm;

    private Map<String, Object> geometry;
    private Map<String, Object> center;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}