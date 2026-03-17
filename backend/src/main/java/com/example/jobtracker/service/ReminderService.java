package com.example.jobtracker.service;

import com.example.jobtracker.dto.ReminderDto;
import com.example.jobtracker.dto.UpsertReminderRequest;
import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.ReminderEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.entity.enums.ReminderStatus;
import com.example.jobtracker.repository.ReminderRepository;
import com.example.jobtracker.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final ApplicationAccessService applicationAccessService;
    private final ActivityLogService activityLogService;

    public ReminderService(ReminderRepository reminderRepository,
                           ApplicationAccessService applicationAccessService,
                           ActivityLogService activityLogService) {
        this.reminderRepository = reminderRepository;
        this.applicationAccessService = applicationAccessService;
        this.activityLogService = activityLogService;
    }

    public List<ReminderDto> list(String username) {
        UserEntity user = applicationAccessService.findUser(username);
        return reminderRepository.findByUserOrderByDueAtAsc(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ReminderDto> upcoming(String username) {
        UserEntity user = applicationAccessService.findUser(username);
        return reminderRepository.findByUserAndStatusAndDueAtGreaterThanEqualOrderByDueAtAsc(user, ReminderStatus.PENDING, Instant.now())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ReminderDto> overdue(String username) {
        UserEntity user = applicationAccessService.findUser(username);
        return reminderRepository.findByUserAndStatusAndDueAtLessThanOrderByDueAtAsc(user, ReminderStatus.PENDING, Instant.now())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ReminderDto create(String username, UpsertReminderRequest request) {
        UserEntity user = applicationAccessService.findUser(username);

        ReminderEntity entity = new ReminderEntity();
        entity.setUser(user);
        applyRequest(user, entity, request);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        ReminderEntity saved = reminderRepository.save(entity);
        activityLogService.log(user, saved.getApplication(), "REMINDER_CREATED", "Created reminder " + saved.getTitle());
        return toDto(saved);
    }

    public ReminderDto update(String username, Long id, UpsertReminderRequest request) {
        UserEntity user = applicationAccessService.findUser(username);

        ReminderEntity entity = reminderRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));

        applyRequest(user, entity, request);
        entity.setUpdatedAt(Instant.now());

        ReminderEntity saved = reminderRepository.save(entity);
        activityLogService.log(user, saved.getApplication(), "REMINDER_UPDATED", "Updated reminder " + saved.getTitle());
        return toDto(saved);
    }

    public void delete(String username, Long id) {
        UserEntity user = applicationAccessService.findUser(username);

        ReminderEntity entity = reminderRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));

        reminderRepository.delete(entity);
        activityLogService.log(user, entity.getApplication(), "REMINDER_DELETED", "Deleted reminder " + entity.getTitle());
    }

    private void applyRequest(UserEntity user, ReminderEntity entity, UpsertReminderRequest request) {
        ApplicationEntity application = null;
        if (request.getApplicationId() != null) {
            application = applicationAccessService.findActiveApplication(user, request.getApplicationId());
        }

        entity.setApplication(application);
        entity.setTitle(request.getTitle().trim());
        entity.setDescription(request.getDescription());
        entity.setDueAt(request.getDueAt());
        entity.setStatus(request.getStatus());
        entity.setReminderType(request.getReminderType());
    }

    private ReminderDto toDto(ReminderEntity entity) {
        ReminderDto dto = new ReminderDto();
        dto.setId(entity.getId());
        dto.setApplicationId(entity.getApplication() != null ? entity.getApplication().getId() : null);
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setDueAt(entity.getDueAt());
        dto.setStatus(entity.getStatus());
        dto.setReminderType(entity.getReminderType());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
