package com.NagarSetu.Backend.User.DTOs;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateComplaintDTO {
    private UUID userId;
    private String title;
    private String description;
    private String department;

    private Object location;
    private List<String> photoUrls;


}
