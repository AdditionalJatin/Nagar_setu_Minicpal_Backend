package com.NagarSetu.Backend.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;



@Entity
@Table(name = "complaint_photos",
        indexes = {
                @Index(name = "idx_photo_complaint", columnList = "complaint_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintPhoto {

    @Id
    @GeneratedValue
    private UUID id;

    private String fileUrl;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;
}