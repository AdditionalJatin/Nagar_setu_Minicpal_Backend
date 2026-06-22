package com.NagarSetu.Backend.User.DTOs;


import com.NagarSetu.Backend.Entities.ComplaintStatus;
import com.NagarSetu.Backend.Entities.Department;
import com.NagarSetu.Backend.Entities.Priority;
import com.NagarSetu.Backend.Entities.UserRole;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateComplaintDTO {
    private UUID ComplaintId;
    private UUID remarkedById;
    private ComplaintStatus status;
    private Priority priority;
    private Department department;
    private String remark;
}
