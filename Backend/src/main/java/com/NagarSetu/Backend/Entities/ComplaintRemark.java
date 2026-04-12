package com.NagarSetu.Backend.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;



@Entity
@Table(name = "complaint_remarks",
        indexes = {
                @Index(name = "idx_remark_complaint_time", columnList = "complaint_id,created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintRemark {

    @Id
    @GeneratedValue
    private UUID id;

    private String remark;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole remarkedBy;


    @Column(columnDefinition = "geometry(Point,4326)",insertable = false, updatable = false)
    private String location;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;




    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
