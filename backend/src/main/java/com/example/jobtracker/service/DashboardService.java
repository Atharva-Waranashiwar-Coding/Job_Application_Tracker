package com.example.jobtracker.service;

import com.example.jobtracker.dto.DashboardSummaryDto;
import com.example.jobtracker.dto.DashboardTrendsDto;
import com.example.jobtracker.dto.MonthlyTrendPointDto;
import com.example.jobtracker.dto.RejectionByStageDto;
import com.example.jobtracker.dto.StageCountDto;
import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.ApplicationStageHistoryEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.entity.enums.ReminderStatus;
import com.example.jobtracker.repository.ApplicationRepository;
import com.example.jobtracker.repository.InterviewRepository;
import com.example.jobtracker.repository.OfferRepository;
import com.example.jobtracker.repository.ReminderRepository;
import com.example.jobtracker.repository.StageHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final DateTimeFormatter MONTH_KEY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM", Locale.ROOT);

    private final ApplicationAccessService applicationAccessService;
    private final ApplicationRepository applicationRepository;
    private final StageService stageService;
    private final InterviewRepository interviewRepository;
    private final OfferRepository offerRepository;
    private final ReminderRepository reminderRepository;
    private final StageHistoryRepository stageHistoryRepository;

    public DashboardService(ApplicationAccessService applicationAccessService,
                            ApplicationRepository applicationRepository,
                            StageService stageService,
                            InterviewRepository interviewRepository,
                            OfferRepository offerRepository,
                            ReminderRepository reminderRepository,
                            StageHistoryRepository stageHistoryRepository) {
        this.applicationAccessService = applicationAccessService;
        this.applicationRepository = applicationRepository;
        this.stageService = stageService;
        this.interviewRepository = interviewRepository;
        this.offerRepository = offerRepository;
        this.reminderRepository = reminderRepository;
        this.stageHistoryRepository = stageHistoryRepository;
    }

    public DashboardSummaryDto summary(String username) {
        UserEntity user = applicationAccessService.findUser(username);

        long total = applicationRepository.countByUserAndDeletedAtIsNull(user);
        List<StageCountDto> stageCounts = new ArrayList<>();
        long appliedCount = 0;

        var stages = stageService.listForUser(user, false);
        for (int i = 0; i < stages.size(); i++) {
            var stage = stages.get(i);
            long count = applicationRepository.countByUserAndDeletedAtIsNullAndStage(user, stage);

            StageCountDto countDto = new StageCountDto();
            countDto.setStageId(stage.getId());
            countDto.setStageName(stage.getName());
            countDto.setCount(count);
            stageCounts.add(countDto);

            if (i == 0 || "applied".equalsIgnoreCase(stage.getName())) {
                appliedCount = count;
            }
        }

        long interviewCount = interviewRepository.countDistinctByApplication_UserAndApplication_DeletedAtIsNull(user);
        long offerCount = offerRepository.countByApplication_UserAndApplication_DeletedAtIsNull(user);
        long rejectionCount = applicationRepository.countByUserAndDeletedAtIsNullAndStage_TerminalRejectionTrue(user);

        long upcomingReminders = reminderRepository
                .countByUserAndStatusAndDueAtGreaterThanEqual(user, ReminderStatus.PENDING, Instant.now());
        long overdueReminders = reminderRepository
                .countByUserAndStatusAndDueAtLessThan(user, ReminderStatus.PENDING, Instant.now());

        long respondedCount = Math.max(total - appliedCount, 0);

        DashboardSummaryDto dto = new DashboardSummaryDto();
        dto.setTotalApplications(total);
        dto.setApplicationsByStage(stageCounts);
        dto.setResponseRate(ratio(respondedCount, total));
        dto.setInterviewConversionRate(ratio(interviewCount, total));
        dto.setOfferCount(offerCount);
        dto.setRejectionCount(rejectionCount);
        dto.setUpcomingReminders(upcomingReminders);
        dto.setOverdueReminders(overdueReminders);
        return dto;
    }

    public DashboardTrendsDto trends(String username, int months) {
        UserEntity user = applicationAccessService.findUser(username);
        int safeMonths = Math.min(Math.max(months, 1), 24);

        Map<String, MonthlyTrendPointDto> monthly = buildMonthlyTemplate(safeMonths);

        List<ApplicationEntity> applications = applicationRepository.findByUserAndDeletedAtIsNull(user);
        for (ApplicationEntity application : applications) {
            LocalDate date = application.getAppliedAt() != null
                    ? application.getAppliedAt()
                    : application.getStageEnteredAt().atZone(ZoneOffset.UTC).toLocalDate();
            String key = date.withDayOfMonth(1).format(MONTH_KEY_FORMAT);
            MonthlyTrendPointDto point = monthly.get(key);
            if (point != null) {
                point.setApplications(point.getApplications() + 1);
            }
        }

        Map<Long, RejectionByStageDto> rejectionsByStage = new LinkedHashMap<>();
        List<ApplicationStageHistoryEntity> history = stageHistoryRepository.findByApplication_UserOrderByChangedAtAsc(user);

        for (ApplicationStageHistoryEntity event : history) {
            if (event.getApplication().getDeletedAt() != null) {
                continue;
            }
            if (event.getToStage() == null || !event.getToStage().isTerminalRejection()) {
                continue;
            }

            String monthKey = event.getChangedAt().atZone(ZoneOffset.UTC).toLocalDate().withDayOfMonth(1).format(MONTH_KEY_FORMAT);
            MonthlyTrendPointDto monthlyPoint = monthly.get(monthKey);
            if (monthlyPoint != null) {
                monthlyPoint.setRejections(monthlyPoint.getRejections() + 1);
            }

            RejectionByStageDto stageDto = rejectionsByStage.computeIfAbsent(event.getToStage().getId(), stageId -> {
                RejectionByStageDto dto = new RejectionByStageDto();
                dto.setStageId(event.getToStage().getId());
                dto.setStageName(event.getToStage().getName());
                dto.setCount(0);
                return dto;
            });
            stageDto.setCount(stageDto.getCount() + 1);
        }

        DashboardTrendsDto dto = new DashboardTrendsDto();
        dto.setMonthly(new ArrayList<>(monthly.values()));
        dto.setRejectionsByStage(new ArrayList<>(rejectionsByStage.values()));
        return dto;
    }

    private Map<String, MonthlyTrendPointDto> buildMonthlyTemplate(int months) {
        Map<String, MonthlyTrendPointDto> map = new LinkedHashMap<>();
        LocalDate cursor = LocalDate.now(ZoneOffset.UTC).withDayOfMonth(1).minusMonths(months - 1L);
        for (int i = 0; i < months; i++) {
            LocalDate monthDate = cursor.plusMonths(i);
            String key = monthDate.format(MONTH_KEY_FORMAT);

            MonthlyTrendPointDto point = new MonthlyTrendPointDto();
            point.setMonth(key);
            point.setApplications(0);
            point.setRejections(0);
            map.put(key, point);
        }
        return map;
    }

    private double ratio(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0;
        }
        double raw = (double) numerator / (double) denominator;
        return Math.round(raw * 100.0) / 100.0;
    }
}
