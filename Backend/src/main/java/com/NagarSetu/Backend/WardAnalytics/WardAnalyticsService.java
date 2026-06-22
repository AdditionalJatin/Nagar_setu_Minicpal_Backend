package com.NagarSetu.Backend.WardAnalytics;

import com.NagarSetu.Backend.Complaint.ComplaintRepository;
import com.NagarSetu.Backend.Entities.Complaint;
import com.NagarSetu.Backend.Entities.ComplaintStatus;
import com.NagarSetu.Backend.Entities.Department;
import com.NagarSetu.Backend.Entities.WardMetricesDashboard.WardAnalytics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class WardAnalyticsService {
    private final WardAnalyticsRepository wardAnalyticsRepository;
    private final ComplaintRepository complaintRepository;

    private static final Set<ComplaintStatus> ACTIVE_STATUSES =
            Set.of(
                    ComplaintStatus.INITIATED,
                    ComplaintStatus.OPEN,
                    ComplaintStatus.ASSIGNED,
                    ComplaintStatus.IN_PROGRESS
            );




    public WardAnalytics getOrCreateTodayAnalytics(
            UUID wardId,
            UUID cityId
    ) {

        LocalDate today = LocalDate.now();

        return wardAnalyticsRepository
                .findByWardIdAndAnalyticsDate(
                        wardId,
                        today
                )
                .orElseGet(() -> {

                    WardAnalytics analytics =
                            WardAnalytics.builder()
                                    .wardId(wardId)
                                    .cityId(cityId)
                                    .analyticsDate(today)
                                    .build();

                    return wardAnalyticsRepository.save(analytics);
                });
    }

    public void onComplaintCreated(
            Complaint complaint
    ) {

        WardAnalytics analytics =
                getOrCreateTodayAnalytics(
                        complaint.getWard().getId(),
                        complaint.getCity().getId()
                );

        analytics.setComplaintsReceived(
                analytics.getComplaintsReceived() + 1
        );

        analytics.incrementDepartment(
                complaint.getDepartment()
        );

        refreshActiveComplaintSnapshot(
                complaint.getWard().getId(),
                analytics
        );

        wardAnalyticsRepository.save(analytics);
    }

    public void onComplaintCompleted(
            Complaint complaint,
           ComplaintStatus oldStatus,
           ComplaintStatus newStatus
    ) {




        WardAnalytics analytics =
                getOrCreateTodayAnalytics(
                        complaint.getWard().getId(),
                        complaint.getCity().getId()
                );

        if(newStatus == ComplaintStatus.RESOLVED ||
                newStatus == ComplaintStatus.CLOSED) {

            analytics.setResolvedComplaints(
                    analytics.getResolvedComplaints() + 1
            );

            long resolutionMinutes =
                    Duration.between(
                            complaint.getCreatedAt(),
                            complaint.getResolvedAt()
                    ).toMinutes();

            analytics.setTotalResolutionMinutes(
                    analytics.getTotalResolutionMinutes()
                            + resolutionMinutes
            );

        }

        refreshActiveComplaintSnapshot(
                complaint.getWard().getId(),
                analytics
        );

        wardAnalyticsRepository.save(analytics);
    }

    public void onComplaintEscalated(
            Complaint complaint
    ) {

        WardAnalytics analytics =
                getOrCreateTodayAnalytics(
                        complaint.getWard().getId(),
                        complaint.getCity().getId()
                );

        analytics.setEscalatedComplaints(
                analytics.getEscalatedComplaints() + 1
        );

        wardAnalyticsRepository.save(analytics);
    }

    public void onDepartmentChanged(
            Complaint complaint,
            Department oldDepartment
    ) {

        WardAnalytics analytics =
                getOrCreateTodayAnalytics(
                        complaint.getWard().getId(),
                        complaint.getCity().getId()
                );

        analytics.decrementDepartment(
                oldDepartment
        );

        analytics.incrementDepartment(
                complaint.getDepartment()
        );

        wardAnalyticsRepository.save(analytics);
    }

    public void refreshActiveComplaintSnapshot(
            UUID wardId,
            WardAnalytics analytics
    ) {

        long activeCount =
                complaintRepository.countByWardIdAndStatusIn(
                        wardId,
                        ACTIVE_STATUSES
                );

        analytics.setActiveComplaints(
                activeCount
        );
    }




}
