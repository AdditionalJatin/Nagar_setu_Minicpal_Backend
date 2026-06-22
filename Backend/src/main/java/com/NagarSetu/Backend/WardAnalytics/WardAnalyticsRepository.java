package com.NagarSetu.Backend.WardAnalytics;

import com.NagarSetu.Backend.Entities.WardMetricesDashboard.WardAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;


@Repository
public interface WardAnalyticsRepository  extends JpaRepository<WardAnalytics, UUID> {
    Optional<WardAnalytics> findByWardIdAndAnalyticsDate(
            UUID wardId,
            LocalDate analyticsDate
    );

    Optional<WardAnalytics> findTopByWardIdOrderByAnalyticsDateDesc(
            UUID wardId
    );

    List<WardAnalytics> findByWardIdAndAnalyticsDateBetweenOrderByAnalyticsDateAsc(
            UUID wardId,
            LocalDate startDate,
            LocalDate endDate
    );

}
