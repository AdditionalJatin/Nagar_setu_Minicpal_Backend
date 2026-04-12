package com.NagarSetu.Backend.User.Services;

import com.NagarSetu.Backend.User.DTOs.RegisterRequestDTO;
import com.NagarSetu.Backend.User.DTOs.RegisterResponseDTO;
import com.NagarSetu.Backend.User.DTOs.UserLoginRequestDTO;

public interface AUTH {
    RegisterResponseDTO signUp(RegisterRequestDTO registerRequestDTO);
    RegisterResponseDTO login(UserLoginRequestDTO userLoginRequestDTO);
}
