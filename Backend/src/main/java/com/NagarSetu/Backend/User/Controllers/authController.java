package com.NagarSetu.Backend.User.Controllers;


import com.NagarSetu.Backend.User.DTOs.*;
import com.NagarSetu.Backend.User.Services.AUTH;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth/user")
@RequiredArgsConstructor
public class authController {
    private final AUTH auth;

    @PostMapping("/signup")
    public ResponseEntity<RegisterResponseDTO> signUp   (@RequestBody RegisterRequestDTO user){
        RegisterResponseDTO response = auth.signUp(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginRequestDTO request, HttpServletResponse res) {
        UserLoginResponseDTO response = auth.login(request,res);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }


    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(auth.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequestDTO request) {
        auth.logout(request);
        return ResponseEntity.ok("Successfully logged out.");
    }

}
