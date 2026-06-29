package com.NagarSetu.Backend.User.Services;

import com.NagarSetu.Backend.User.DTOs.*;
import jakarta.servlet.http.HttpServletResponse;

public interface AUTH {
    RegisterResponseDTO signUp(RegisterRequestDTO registerRequestDTO);
    UserLoginResponseDTO login(UserLoginRequestDTO userLoginRequestDTO, HttpServletResponse response);
    RefreshResponse refresh( RefreshRequest refreshRequestDTO);
    void logout(LogoutRequestDTO logoutRequestDTO);

}
