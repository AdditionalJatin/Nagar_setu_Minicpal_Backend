package com.NagarSetu.Backend.User.Controllers;


import com.NagarSetu.Backend.Complaint.ComplaintRepository;
import com.NagarSetu.Backend.User.DTOs.ComplaintResponseDTO;
import com.NagarSetu.Backend.User.DTOs.CreateComplaintDTO;
import com.NagarSetu.Backend.User.Services.UserServiceComplaint;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class RegisterComplaint {

    private final UserServiceComplaint userServiceComplaint;

    @PostMapping("/complaint")
    public ResponseEntity<ComplaintResponseDTO> registerComplaint(@RequestBody CreateComplaintDTO dto) {
        ComplaintResponseDTO response = userServiceComplaint.registerComplaint(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }


}
