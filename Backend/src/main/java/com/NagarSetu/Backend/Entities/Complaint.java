package com.NagarSetu.Backend.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;



@Entity
@Table(name = "complaints",
        indexes = {
                @Index(name = "idx_city_status_time", columnList = "city_id,status,created_at"),
                @Index(name = "idx_created_at", columnList = "created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    private String description;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;



    @Column(columnDefinition = "geometry(Point,4326)",insertable = false, updatable = false)
    private String location;


    // ⚡ Dashboard optimization
    private String lastRemark;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole remarkedBy;

    private LocalDateTime lastUpdatedAt;
    private Integer photoCount;

    private LocalDateTime createdAt;
    private  LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintType complaintType;

    // 🔗 WHO CREATED
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // 🔗 CITY / WARD
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    // 🔗 RELATIONS
    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL)
    private List<ComplaintPhoto> photos;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL)
    private List<ComplaintRemark> remarks;
}