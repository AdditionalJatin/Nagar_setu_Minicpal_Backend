package com.NagarSetu.Backend.User.Services;

import com.NagarSetu.Backend.Complaint.ComplaintPhotoRepository;
import com.NagarSetu.Backend.Complaint.ComplaintRemarkRepository;
import com.NagarSetu.Backend.Complaint.ComplaintRepository;
import com.NagarSetu.Backend.Entities.*;
import com.NagarSetu.Backend.User.DTOs.ComplaintResponseDTO;
import com.NagarSetu.Backend.User.DTOs.CreateComplaintDTO;
import com.NagarSetu.Backend.User.DTOs.UpdateComplaintDTO;
import com.NagarSetu.Backend.User.UserRepository.UserRepository;
import com.NagarSetu.Backend.WardAnalytics.WardAnalyticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class UserServiceComplaintImpl implements UserServiceComplaint {
    private final EntityManager entityManager;

    private final ComplaintRepository complaintRepository;
    private final ComplaintPhotoRepository complaintPhotoRepository;
    private final ComplaintRemarkRepository complaintRemarkRepository;
    private final UserRepository userRepository;
    private final WardAnalyticsService wardAnalyticsService;
    private final ObjectMapper mapper;


    private static final Set<ComplaintStatus> COMPLETED_STATUSES =
            EnumSet.of(
                    ComplaintStatus.RESOLVED,
                    ComplaintStatus.CLOSED,
                    ComplaintStatus.REJECTED
            );

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
                user.getWard().getId(),
                user.getRole().toString()
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


        wardAnalyticsService.onComplaintCreated(
                complaint
        );

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


    @Override
   public ComplaintResponseDTO updateComplaint(UpdateComplaintDTO DTO){

        if(DTO.getComplaintId() == null||DTO.getComplaintId().toString().isEmpty()){
            throw new IllegalArgumentException("Complaint ID cannot be null");
        }
        if(DTO.getRemarkedById()==null||DTO.getRemarkedById().toString().isEmpty()){
            throw new IllegalArgumentException("Remarked By ID cannot be null");
        }

         User user = userRepository.findById(DTO.getRemarkedById())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + DTO.getRemarkedById()));

         Complaint complaint = complaintRepository.findById(DTO.getComplaintId())
                 .orElseThrow(() -> new IllegalArgumentException("Complaint not found with ID: " + DTO.getComplaintId()));

         ComplaintStatus oldStatus = complaint.getStatus();
         ComplaintStatus newStatus = DTO.getStatus() != null ? DTO.getStatus() : complaint.getStatus();

        if (COMPLETED_STATUSES.contains(oldStatus)) {
            throw new IllegalStateException(
                    "Complaint is already completed"
            );
        }

        if (DTO.getStatus() != null) {

            complaint.setStatus(DTO.getStatus());

            if (DTO.getStatus() == ComplaintStatus.RESOLVED) {
                complaint.setResolvedAt(LocalDateTime.now());
            }
        }

        if(DTO.getPriority() != null){
            complaint.setPriority(DTO.getPriority());
        }

        if (DTO.getRemark() != null && !DTO.getRemark().isBlank()) {
            complaint.setLastRemark(DTO.getRemark());
        }

        complaint.setRemarkedByRole(user.getRole());
        complaint.setLastUpdatedAt(LocalDateTime.now());
        complaint.setUpdatedAt(LocalDateTime.now());
        complaintRepository.save(complaint);

        ComplaintRemark remark = ComplaintRemark.builder()
                .complaint(complaint)
                .remark(
                        DTO.getRemark() == null
                                ? "Complaint Updated"
                                : DTO.getRemark()
                )
                .status(complaint.getStatus())
                .priority(complaint.getPriority())
                .remarkedBy(user.getRole())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        complaintRemarkRepository.save(remark);

        // Analytics



        boolean wasCompleted = COMPLETED_STATUSES.contains(oldStatus);
        boolean isCompleted = COMPLETED_STATUSES.contains(newStatus);

        if (!wasCompleted && isCompleted) {
            wardAnalyticsService.onComplaintCompleted(
                    complaint,
                    oldStatus,
                    newStatus
            );
        }


          return ComplaintResponseDTO.builder()
                .id(complaint.getId())
                .title(complaint.getTitle())
                .description(complaint.getDescription())
                .department(complaint.getDepartment().getDisplayName())
                .status(complaint.getStatus().name())
                .priority(complaint.getPriority().name())
                .build();
    }



}
