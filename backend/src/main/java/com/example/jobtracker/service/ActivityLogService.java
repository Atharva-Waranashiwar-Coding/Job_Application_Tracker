package com.example.jobtracker.service;

import com.example.jobtracker.dto.ActivityLogDto;
import com.example.jobtracker.entity.ActivityLogEntity;
import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public ActivityLogDto log(UserEntity user, ApplicationEntity application, String action, String details) {
        ActivityLogEntity entity = new ActivityLogEntity();
        entity.setUser(user);
        entity.setApplication(application);
        entity.setAction(action);
        entity.setDetails(details);
        ActivityLogEntity saved = activityLogRepository.save(entity);
        return toDto(saved);
    }

    public List<ActivityLogDto> listForUser(UserEntity user) {
        return activityLogRepository.findByUser(user).stream().map(this::toDto).collect(Collectors.toList());
    }

    private ActivityLogDto toDto(ActivityLogEntity entity) {
        ActivityLogDto dto = new ActivityLogDto();
        dto.setId(entity.getId());
        dto.setApplicationId(entity.getApplication() != null ? entity.getApplication().getId() : null);
        dto.setAction(entity.getAction());
        dto.setDetails(entity.getDetails());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
