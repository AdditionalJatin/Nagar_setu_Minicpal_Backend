package com.NagarSetu.Backend.User.Controllers;


import com.NagarSetu.Backend.User.DTOs.RegisterRequestDTO;
import com.NagarSetu.Backend.User.DTOs.RegisterResponseDTO;
import com.NagarSetu.Backend.User.DTOs.UserLoginRequestDTO;
import com.NagarSetu.Backend.User.Services.AUTH;
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
    public ResponseEntity<RegisterResponseDTO> login(@RequestBody UserLoginRequestDTO request) {
        return ResponseEntity.ok(auth.login(request));
    }


}
