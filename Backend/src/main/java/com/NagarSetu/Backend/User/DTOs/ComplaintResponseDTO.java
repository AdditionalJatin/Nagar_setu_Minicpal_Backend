package com.NagarSetu.Backend.User.DTOs;


import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintResponseDTO {
    private UUID id;
    private String title;
    private String description;
    private String department;
    private String status;
    private String priority;
    private Map<String, Object> location;

}
