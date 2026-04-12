package com.NagarSetu.Backend.Entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "city_name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(length = 100)
    private String country = "India";

    @Column(columnDefinition = "geometry(Polygon,4326)",  insertable = false, updatable = false)
    private String geometry;

    @Column(columnDefinition = "geometry(Point,4326)", insertable = false, updatable = false)
    private String center;

    private Long population;

    private Double areaSqKm;

    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_admin_id")
    private User cityAdmin;


    // RELATIONS
    @JsonIgnore
    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ward> wards;

    @JsonIgnore
    @OneToMany(mappedBy = "city")
    private List<User> users;

    // lifecycle
    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();

        updatedAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
