package com.NagarSetu.Backend.MasterAdmin.DTOs;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class RegisterWardDTO {
    private String name;
    private UUID cityId;
    private Integer wardNumber;
    private Long population;

    private Map<String, Object> geometry;

}
