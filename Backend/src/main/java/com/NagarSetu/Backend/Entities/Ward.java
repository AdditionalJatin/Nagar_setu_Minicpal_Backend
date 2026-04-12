package com.NagarSetu.Backend.Entities;


import com.NagarSetu.Backend.Entities.User;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Polygon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "wards",uniqueConstraints = @UniqueConstraint(columnNames = {"city_id", "ward_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ward {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column( nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(nullable = false, name = "ward_number")
    private Integer wardNumber;

    @Column(columnDefinition = "geometry(Polygon,4326)", insertable = false, updatable = false)
    private String geometry;

    @Column(columnDefinition = "geometry(Point,4326)", insertable = false, updatable = false)
    private String center;


    private Integer population;

    private Double areaSqKm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_head_id")
    private User wardHead;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // RELATIONS

    @OneToMany(mappedBy = "ward")
    private List<User> users;

    // lifecycle
    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
