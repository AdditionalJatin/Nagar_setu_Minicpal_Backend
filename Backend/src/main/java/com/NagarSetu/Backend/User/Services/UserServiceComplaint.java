package com.NagarSetu.Backend.User.Services;

import com.NagarSetu.Backend.User.DTOs.ComplaintResponseDTO;
import com.NagarSetu.Backend.User.DTOs.CreateComplaintDTO;
import com.NagarSetu.Backend.User.DTOs.UpdateComplaintDTO;

public interface UserServiceComplaint {

    ComplaintResponseDTO registerComplaint(CreateComplaintDTO DTO);

    ComplaintResponseDTO updateComplaint(UpdateComplaintDTO DTO);

}
