package com.NagarSetu.Backend.Entities.WardMetricesDashboard;



import com.NagarSetu.Backend.Entities.Department;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "ward_analytics",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ward_analytics_date",
                        columnNames = {"ward_id", "analytics_date"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_ward_analytics_ward_date",
                        columnList = "ward_id, analytics_date"
                ),
                @Index(
                        name = "idx_ward_analytics_date",
                        columnList = "analytics_date"
                ),
                @Index(
                        name = "idx_ward_analytics_city",
                        columnList = "city_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WardAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Ward for which analytics is maintained.
     */
    @Column(name = "ward_id", nullable = false)
    private UUID wardId;

    /**
     * City reference for city level aggregation.
     */
    @Column(name = "city_id", nullable = false)
    private UUID cityId;

    /**
     * One row per ward per day.
     */
    @Column(name = "analytics_date", nullable = false)
    private LocalDate analyticsDate;

    // =====================================================
    // DAILY COMPLAINT METRICS
    // =====================================================

    /**
     * New complaints received on this day.
     */
    @Builder.Default
    @Column(name = "complaints_received", nullable = false)
    private Long complaintsReceived = 0L;

    /**
     * Active complaints at end of day.
     */
    @Builder.Default
    @Column(name = "active_complaints", nullable = false)
    private Long activeComplaints = 0L;

    /**
     * Complaints resolved on this day.
     */
    @Builder.Default
    @Column(name = "resolved_complaints", nullable = false)
    private Long resolvedComplaints = 0L;

    /**
     * Escalated complaints on this day.
     */
    @Builder.Default
    @Column(name = "escalated_complaints", nullable = false)
    private Long escalatedComplaints = 0L;

    /**
     * Total resolution time of all resolved complaints.
     * Stored in minutes.
     */
    @Builder.Default
    @Column(name = "total_resolution_minutes", nullable = false)
    private Long totalResolutionMinutes = 0L;

    // =====================================================
    // DEPARTMENT WISE COUNTS
    // =====================================================

    @Builder.Default
    @Column(name = "sanitation_complaints", nullable = false)
    private Long sanitationComplaints = 0L;

    @Builder.Default
    @Column(name = "water_supply_complaints", nullable = false)
    private Long waterSupplyComplaints = 0L;

    @Builder.Default
    @Column(name = "electricity_complaints", nullable = false)
    private Long electricityComplaints = 0L;

    @Builder.Default
    @Column(name = "roads_complaints", nullable = false)
    private Long roadsComplaints = 0L;

    @Builder.Default
    @Column(name = "drainage_complaints", nullable = false)
    private Long drainageComplaints = 0L;

    @Builder.Default
    @Column(name = "public_health_complaints", nullable = false)
    private Long publicHealthComplaints = 0L;

    @Builder.Default
    @Column(name = "other_complaints", nullable = false)
    private Long otherComplaints = 0L;

    // =====================================================
    // AUDIT
    // =====================================================

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.lastUpdatedAt = now;

        if (this.analyticsDate == null) {
            this.analyticsDate = LocalDate.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }


    public void incrementDepartment(Department department) {

        switch (department) {

            case SANITATION -> sanitationComplaints++;

            case WATER_SUPPLY -> waterSupplyComplaints++;

            case ELECTRICITY -> electricityComplaints++;

            case ROADS -> roadsComplaints++;

            case DRAINAGE -> drainageComplaints++;

            case PUBLIC_HEALTH -> publicHealthComplaints++;

            default -> otherComplaints++;
        }
    }

    public void decrementDepartment(Department department) {

        switch (department) {

            case SANITATION -> sanitationComplaints--;

            case WATER_SUPPLY -> waterSupplyComplaints--;

            case ELECTRICITY -> electricityComplaints--;

            case ROADS -> roadsComplaints--;

            case DRAINAGE -> drainageComplaints--;

            case PUBLIC_HEALTH -> publicHealthComplaints--;

            default -> otherComplaints--;
        }
    }





}

