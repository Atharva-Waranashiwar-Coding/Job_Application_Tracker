package com.example.jobtracker.service;

import com.example.jobtracker.dto.StageHistoryDto;
import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.ApplicationStageEntity;
import com.example.jobtracker.entity.ApplicationStageHistoryEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.repository.ApplicationRepository;
import com.example.jobtracker.repository.StageHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StageTransitionService {

    private final StageService stageService;
    private final ApplicationRepository applicationRepository;
    private final StageHistoryRepository stageHistoryRepository;
    private final ActivityLogService activityLogService;
    private final ApplicationAccessService applicationAccessService;

    public StageTransitionService(StageService stageService,
                                  ApplicationRepository applicationRepository,
                                  StageHistoryRepository stageHistoryRepository,
                                  ActivityLogService activityLogService,
                                  ApplicationAccessService applicationAccessService) {
        this.stageService = stageService;
        this.applicationRepository = applicationRepository;
        this.stageHistoryRepository = stageHistoryRepository;
        this.activityLogService = activityLogService;
        this.applicationAccessService = applicationAccessService;
    }

    public ApplicationEntity transition(UserEntity user, ApplicationEntity application, Long toStageId, String reason) {
        applicationAccessService.ensureApplicationEditable(application);

        ApplicationStageEntity fromStage = application.getStage();
        if (fromStage != null && fromStage.getId().equals(toStageId)) {
            return application;
        }

        ApplicationStageEntity toStage = stageService.getStageForUser(user, toStageId);

        application.setStage(toStage);
        application.setStageEnteredAt(Instant.now());
        application.setUpdatedAt(Instant.now());
        ApplicationEntity saved = applicationRepository.save(application);

        recordHistory(saved, fromStage, toStage, reason == null || reason.isBlank() ? "Stage updated" : reason);
        activityLogService.log(
                user,
                saved,
                "APPLICATION_STAGE_CHANGED",
                "Moved application " + saved.getId() + " from " + (fromStage == null ? "-" : fromStage.getName()) + " to " + toStage.getName()
        );

        return saved;
    }

    public void recordInitialStage(ApplicationEntity application, String reason) {
        recordHistory(application, null, application.getStage(), reason);
    }

    public List<StageHistoryDto> listHistory(ApplicationEntity application) {
        return stageHistoryRepository.findByApplicationOrderByChangedAtDesc(application)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private void recordHistory(ApplicationEntity application,
                               ApplicationStageEntity fromStage,
                               ApplicationStageEntity toStage,
                               String reason) {
        ApplicationStageHistoryEntity history = new ApplicationStageHistoryEntity();
        history.setApplication(application);
        history.setFromStage(fromStage);
        history.setToStage(toStage);
        history.setChangedAt(Instant.now());
        history.setReason(reason);
        stageHistoryRepository.save(history);
    }

    private StageHistoryDto toDto(ApplicationStageHistoryEntity entity) {
        StageHistoryDto dto = new StageHistoryDto();
        dto.setId(entity.getId());
        dto.setFromStageId(entity.getFromStage() != null ? entity.getFromStage().getId() : null);
        dto.setFromStageName(entity.getFromStage() != null ? entity.getFromStage().getName() : null);
        dto.setToStageId(entity.getToStage().getId());
        dto.setToStageName(entity.getToStage().getName());
        dto.setChangedAt(entity.getChangedAt());
        dto.setReason(entity.getReason());
        return dto;
    }
}
