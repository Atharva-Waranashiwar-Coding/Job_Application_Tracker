package com.example.jobtracker.service;

import com.example.jobtracker.dto.ApplicationDto;
import com.example.jobtracker.dto.CreateApplicationRequest;
import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.repository.ApplicationRepository;
import com.example.jobtracker.repository.UserRepository;
import com.example.jobtracker.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              UserRepository userRepository,
                              ActivityLogService activityLogService) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    public List<ApplicationDto> listForUser(String username) {
        UserEntity user = findUser(username);
        return applicationRepository.findByUser(user).stream().map(this::toDto).collect(Collectors.toList());
    }

    public ApplicationDto getForUser(String username, Long id) {
        UserEntity user = findUser(username);
        ApplicationEntity application = applicationRepository.findById(id)
                .filter(app -> app.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));

        return toDto(application);
    }

    public ApplicationDto createForUser(String username, CreateApplicationRequest request) {
        UserEntity user = findUser(username);

        ApplicationEntity entity = new ApplicationEntity();
        entity.setUser(user);
        entity.setCompany(request.getCompany());
        entity.setRole(request.getRole());
        entity.setStatus(request.getStatus());
        entity.setAppliedAt(request.getAppliedAt());
        entity.setSourceUrl(request.getSourceUrl());
        entity.setNotes(request.getNotes());

        ApplicationEntity saved = applicationRepository.save(entity);
        activityLogService.log(user, saved, "APPLICATION_CREATED", "Created application for " + request.getCompany());
        return toDto(saved);
    }

    public ApplicationDto updateForUser(String username, Long id, CreateApplicationRequest request) {
        UserEntity user = findUser(username);
        ApplicationEntity entity = applicationRepository.findById(id)
                .filter(app -> app.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));

        entity.setCompany(request.getCompany());
        entity.setRole(request.getRole());
        entity.setStatus(request.getStatus());
        entity.setAppliedAt(request.getAppliedAt());
        entity.setSourceUrl(request.getSourceUrl());
        entity.setNotes(request.getNotes());
        entity.setUpdatedAt(java.time.Instant.now());

        ApplicationEntity updated = applicationRepository.save(entity);
        activityLogService.log(user, updated, "APPLICATION_UPDATED", "Updated application " + updated.getId());
        return toDto(updated);
    }

    public void deleteForUser(String username, Long id) {
        UserEntity user = findUser(username);
        ApplicationEntity entity = applicationRepository.findById(id)
                .filter(app -> app.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));

        applicationRepository.delete(entity);
        activityLogService.log(user, entity, "APPLICATION_DELETED", "Deleted application " + id);
    }

    public Map<String, Long> statusCounts(String username) {
        UserEntity user = findUser(username);
        List<ApplicationEntity> list = applicationRepository.findByUser(user);
        Map<String, Long> counts = new HashMap<>();
        list.forEach(app -> counts.merge(app.getStatus(), 1L, Long::sum));
        return counts;
    }

    private UserEntity findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private ApplicationDto toDto(ApplicationEntity entity) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(entity.getId());
        dto.setCompany(entity.getCompany());
        dto.setRole(entity.getRole());
        dto.setStatus(entity.getStatus());
        dto.setAppliedAt(entity.getAppliedAt());
        dto.setSourceUrl(entity.getSourceUrl());
        dto.setNotes(entity.getNotes());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
