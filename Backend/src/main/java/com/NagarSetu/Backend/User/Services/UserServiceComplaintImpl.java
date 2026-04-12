package com.NagarSetu.Backend.User.Services;

import com.NagarSetu.Backend.Complaint.ComplaintPhotoRepository;
import com.NagarSetu.Backend.Complaint.ComplaintRemarkRepository;
import com.NagarSetu.Backend.Complaint.ComplaintRepository;
import com.NagarSetu.Backend.Entities.*;
import com.NagarSetu.Backend.User.DTOs.ComplaintResponseDTO;
import com.NagarSetu.Backend.User.DTOs.CreateComplaintDTO;
import com.NagarSetu.Backend.User.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceComplaintImpl implements UserServiceComplaint {
    private final EntityManager entityManager;

    private final ComplaintRepository complaintRepository;
    private final ComplaintPhotoRepository complaintPhotoRepository;
    private final ComplaintRemarkRepository complaintRemarkRepository;
    private final UserRepository userRepository;
    private final ObjectMapper mapper;


    @Override
    public ComplaintResponseDTO registerComplaint(CreateComplaintDTO DTO) {

        if(DTO.getUserId() == null){
            throw new IllegalArgumentException("User ID cannot be null");
        }


        User user = userRepository.findById(DTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + DTO.getUserId()));

        if(DTO.getLocation() == null||DTO.getLocation().toString().isEmpty()){
            throw new IllegalArgumentException("Location cannot be null");
        }
        Department dept = Department.fromString(DTO.getDepartment());
        String priority = dept.getPriority().name();

        String geoJson;
        try {
            geoJson = mapper.writeValueAsString(DTO.getLocation());
        } catch (Exception e) {
            throw new RuntimeException("Invalid GeoJSON");
        }

        Map<String, Object> result = complaintRepository.registerComplaint(
                DTO.getTitle(),
                DTO.getDescription(),
                DTO.getDepartment(),
                priority,
                DTO.getUserId(),
                geoJson,
                user.getWard().getId()
        );

        Complaint complaint = complaintRepository.findById((UUID)result.get("id"))
                .orElseThrow();


        ComplaintRemark remark = ComplaintRemark.builder()
                .complaint(complaint)
                .remark("Complaint registered")
                .status(ComplaintStatus.INITIATED)
                .remarkedBy(user.getRole())
                .priority(complaint.getPriority())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        complaintRemarkRepository.save(remark);

        Map<String, Object> locationMap = null;
        try {
            locationMap = result.get("location") != null
                    ? mapper.readValue((String) result.get("location"), Map.class)
                    : null;
        } catch (Exception e) {
            locationMap = null;
        }


    return ComplaintResponseDTO.builder()
                .id((UUID)complaint.getId())
                .title(complaint.getTitle())
                .description(complaint.getDescription())
                .department(dept.getDisplayName())
                .status(complaint.getStatus().name())
                .priority(complaint.getPriority().name())
                .location(locationMap)
                .build();



    }



}
