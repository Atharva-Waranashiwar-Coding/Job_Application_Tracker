package com.example.jobtracker.service;

import com.example.jobtracker.dto.ApplicationDto;
import com.example.jobtracker.dto.CreateApplicationRequest;
import com.example.jobtracker.dto.PagedResponse;
import com.example.jobtracker.dto.StageHistoryDto;
import com.example.jobtracker.dto.StageTransitionRequest;
import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.entity.enums.ApplicationPriority;
import com.example.jobtracker.entity.enums.WorkMode;
import com.example.jobtracker.repository.ApplicationRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ActivityLogService activityLogService;
    private final ApplicationAccessService applicationAccessService;
    private final StageService stageService;
    private final StageTransitionService stageTransitionService;
    private final int stalledDaysThreshold;

    public ApplicationService(ApplicationRepository applicationRepository,
                              ActivityLogService activityLogService,
                              ApplicationAccessService applicationAccessService,
                              StageService stageService,
                              StageTransitionService stageTransitionService,
                              @Value("${app.pipeline.stalled-days-threshold:14}") int stalledDaysThreshold) {
        this.applicationRepository = applicationRepository;
        this.activityLogService = activityLogService;
        this.applicationAccessService = applicationAccessService;
        this.stageService = stageService;
        this.stageTransitionService = stageTransitionService;
        this.stalledDaysThreshold = stalledDaysThreshold;
    }

    public PagedResponse<ApplicationDto> listForUser(String username,
                                                     int page,
                                                     int size,
                                                     String q,
                                                     Long stageId,
                                                     String location,
                                                     WorkMode workMode,
                                                     Boolean sponsorship,
                                                     String source,
                                                     LocalDate fromAppliedAt,
                                                     LocalDate toAppliedAt,
                                                     String sortBy,
                                                     String sortDir,
                                                     boolean includeDeleted) {
        UserEntity user = applicationAccessService.findUser(username);
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);

        Specification<ApplicationEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user"), user));

            if (includeDeleted) {
                predicates.add(cb.isNotNull(root.get("deletedAt")));
            } else {
                predicates.add(cb.isNull(root.get("deletedAt")));
            }

            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("company")), like),
                        cb.like(cb.lower(root.get("role")), like),
                        cb.like(cb.lower(root.get("notes")), like),
                        cb.like(cb.lower(root.get("location")), like)
                ));
            }

            if (stageId != null) {
                predicates.add(cb.equal(root.get("stage").get("id"), stageId));
            }
            if (location != null && !location.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.trim().toLowerCase(Locale.ROOT) + "%"));
            }
            if (workMode != null) {
                predicates.add(cb.equal(root.get("workMode"), workMode));
            }
            if (sponsorship != null) {
                predicates.add(cb.equal(root.get("sponsorshipAvailable"), sponsorship));
            }
            if (source != null && !source.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("applicationSource")), source.trim().toLowerCase(Locale.ROOT)));
            }
            if (fromAppliedAt != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("appliedAt"), fromAppliedAt));
            }
            if (toAppliedAt != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("appliedAt"), toAppliedAt));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<ApplicationEntity> pageData = applicationRepository.findAll(spec, pageable);

        PagedResponse<ApplicationDto> response = new PagedResponse<>();
        response.setItems(pageData.getContent().stream().map(this::toDto).collect(Collectors.toList()));
        response.setTotal(pageData.getTotalElements());
        response.setPage(pageData.getNumber());
        response.setSize(pageData.getSize());
        response.setHasNext(pageData.hasNext());
        return response;
    }

    // Backward-compatible helper used by older tests/controllers.
    public List<ApplicationDto> listForUser(String username) {
        UserEntity user = applicationAccessService.findUser(username);
        return applicationRepository.findByUserAndDeletedAtIsNull(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ApplicationDto getForUser(String username, Long id) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, id);
        return toDto(application);
    }

    public ApplicationDto createForUser(String username, CreateApplicationRequest request) {
        UserEntity user = applicationAccessService.findUser(username);

        ApplicationEntity entity = new ApplicationEntity();
        entity.setUser(user);
        entity.setStage(request.getStageId() != null
                ? stageService.getStageForUser(user, request.getStageId())
                : stageService.getDefaultStageForUser(user));
        entity.setStageEnteredAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        applyMutableFields(entity, request);

        ApplicationEntity saved = applicationRepository.save(entity);
        stageTransitionService.recordInitialStage(saved, "Application created");
        activityLogService.log(user, saved, "APPLICATION_CREATED", "Created application for " + saved.getCompany());
        return toDto(saved);
    }

    public ApplicationDto updateForUser(String username, Long id, CreateApplicationRequest request) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity entity = applicationAccessService.findActiveApplication(user, id);

        if (request.getStageId() != null
                && (entity.getStage() == null || !entity.getStage().getId().equals(request.getStageId()))) {
            entity = stageTransitionService.transition(user, entity, request.getStageId(), "Stage updated from application form");
        }

        applyMutableFields(entity, request);
        entity.setUpdatedAt(Instant.now());

        ApplicationEntity updated = applicationRepository.save(entity);
        activityLogService.log(user, updated, "APPLICATION_UPDATED", "Updated application " + updated.getId());
        return toDto(updated);
    }

    public ApplicationDto moveStageForUser(String username, Long id, StageTransitionRequest request) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity entity = applicationAccessService.findActiveApplication(user, id);
        ApplicationEntity updated = stageTransitionService.transition(user, entity, request.getToStageId(), request.getReason());
        return toDto(updated);
    }

    public void deleteForUser(String username, Long id) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity entity = applicationAccessService.findActiveApplication(user, id);

        entity.setDeletedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        applicationRepository.save(entity);
        activityLogService.log(user, entity, "APPLICATION_SOFT_DELETED", "Soft deleted application " + id);
    }

    public ApplicationDto restoreForUser(String username, Long id) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity entity = applicationAccessService.findApplication(user, id);
        if (entity.getDeletedAt() == null) {
            return toDto(entity);
        }

        entity.setDeletedAt(null);
        entity.setUpdatedAt(Instant.now());
        ApplicationEntity restored = applicationRepository.save(entity);
        activityLogService.log(user, restored, "APPLICATION_RESTORED", "Restored application " + id);
        return toDto(restored);
    }

    public Map<String, Long> statusCounts(String username) {
        UserEntity user = applicationAccessService.findUser(username);
        Map<String, Long> counts = new LinkedHashMap<>();
        stageService.listForUser(user, false).forEach(stage -> {
            long count = applicationRepository.countByUserAndDeletedAtIsNullAndStage(user, stage);
            counts.put(stage.getName(), count);
        });
        return counts;
    }

    public List<StageHistoryDto> stageHistoryForUser(String username, Long id) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findApplication(user, id);
        return stageTransitionService.listHistory(application);
    }

    public ApplicationDto toDto(ApplicationEntity entity) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(entity.getId());
        dto.setCompany(entity.getCompany());
        dto.setRole(entity.getRole());
        dto.setStage(entity.getStage() != null ? stageService.toDto(entity.getStage()) : null);
        dto.setStageEnteredAt(entity.getStageEnteredAt());
        dto.setDaysInCurrentStage(daysInCurrentStage(entity.getStageEnteredAt()));
        dto.setStalled(isStalled(entity));
        dto.setPriority(entity.getPriority());
        dto.setAppliedAt(entity.getAppliedAt());
        dto.setSourceUrl(entity.getSourceUrl());
        dto.setNotes(entity.getNotes());
        dto.setUpdatedAt(entity.getUpdatedAt());

        dto.setLocation(entity.getLocation());
        dto.setWorkMode(entity.getWorkMode());
        dto.setEmploymentType(entity.getEmploymentType());
        dto.setSalaryRange(entity.getSalaryRange());
        dto.setSponsorshipAvailable(entity.getSponsorshipAvailable());
        dto.setApplicationSource(entity.getApplicationSource());
        dto.setApplicationDeadline(entity.getApplicationDeadline());
        dto.setPostingUrl(entity.getPostingUrl());
        dto.setJobDescription(entity.getJobDescription());
        return dto;
    }

    private void applyMutableFields(ApplicationEntity entity, CreateApplicationRequest request) {
        entity.setCompany(request.getCompany().trim());
        entity.setRole(request.getRole().trim());
        entity.setPriority(request.getPriority() == null ? ApplicationPriority.MEDIUM : request.getPriority());
        entity.setAppliedAt(request.getAppliedAt());
        entity.setSourceUrl(request.getSourceUrl());
        entity.setNotes(request.getNotes());

        entity.setLocation(request.getLocation());
        entity.setWorkMode(request.getWorkMode());
        entity.setEmploymentType(request.getEmploymentType());
        entity.setSalaryRange(request.getSalaryRange());
        entity.setSponsorshipAvailable(request.getSponsorshipAvailable());
        entity.setApplicationSource(request.getApplicationSource());
        entity.setApplicationDeadline(request.getApplicationDeadline());
        entity.setPostingUrl(request.getPostingUrl());
        entity.setJobDescription(request.getJobDescription());
    }

    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        String normalizedSortBy = sortBy == null ? "STAGE_UPDATED" : sortBy.trim().toUpperCase(Locale.ROOT);
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        String sortProperty;
        switch (normalizedSortBy) {
            case "MOST_RECENT":
            case "UPDATED":
                sortProperty = "updatedAt";
                break;
            case "OLDEST":
                sortProperty = "updatedAt";
                direction = Sort.Direction.ASC;
                break;
            case "DEADLINE":
                sortProperty = "applicationDeadline";
                break;
            case "PRIORITY":
                sortProperty = "priority";
                break;
            case "APPLIED_AT":
                sortProperty = "appliedAt";
                break;
            case "STAGE_UPDATED":
            default:
                sortProperty = "stageEnteredAt";
                break;
        }

        return PageRequest.of(safePage, safeSize, Sort.by(direction, sortProperty));
    }

    private long daysInCurrentStage(Instant stageEnteredAt) {
        if (stageEnteredAt == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(stageEnteredAt, Instant.now());
    }

    private boolean isStalled(ApplicationEntity entity) {
        if (entity.getStageEnteredAt() == null || entity.getDeletedAt() != null) {
            return false;
        }
        long daysInStage = daysInCurrentStage(entity.getStageEnteredAt());
        return daysInStage >= stalledDaysThreshold;
    }
}
