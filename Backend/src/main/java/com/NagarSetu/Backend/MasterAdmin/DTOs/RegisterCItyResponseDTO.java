package com.NagarSetu.Backend.MasterAdmin.DTOs;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor   // ✅ makes constructor public
@NoArgsConstructor
@Builder
public class RegisterCItyResponseDTO {
    private UUID id;
    private String name;
    private String state;
    private String country;
    private Long population;
    private Double areaSqKm;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Returns GeoJSON object to frontend (not a raw string)
    private Map<String, Object> geometry;
    private Map<String, Object> center;

}
