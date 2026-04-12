package com.NagarSetu.Backend.User.Services;

import com.NagarSetu.Backend.User.DTOs.ComplaintResponseDTO;
import com.NagarSetu.Backend.User.DTOs.CreateComplaintDTO;

public interface UserServiceComplaint {

    ComplaintResponseDTO registerComplaint(CreateComplaintDTO DTO);

}
