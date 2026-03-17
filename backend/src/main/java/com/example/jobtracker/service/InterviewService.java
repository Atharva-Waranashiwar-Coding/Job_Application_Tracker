package com.example.jobtracker.service;

import com.example.jobtracker.dto.InterviewRoundDto;
import com.example.jobtracker.dto.UpsertInterviewRoundRequest;
import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.InterviewRoundEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.repository.InterviewRepository;
import com.example.jobtracker.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationAccessService applicationAccessService;
    private final ActivityLogService activityLogService;

    public InterviewService(InterviewRepository interviewRepository,
                            ApplicationAccessService applicationAccessService,
                            ActivityLogService activityLogService) {
        this.interviewRepository = interviewRepository;
        this.applicationAccessService = applicationAccessService;
        this.activityLogService = activityLogService;
    }

    public List<InterviewRoundDto> listForApplication(String username, Long applicationId) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        return interviewRepository.findByApplicationOrderByScheduledAtAsc(application)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public InterviewRoundDto create(String username, Long applicationId, UpsertInterviewRoundRequest request) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        InterviewRoundEntity entity = new InterviewRoundEntity();
        entity.setApplication(application);
        applyRequest(entity, request);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        InterviewRoundEntity saved = interviewRepository.save(entity);
        activityLogService.log(user, application, "INTERVIEW_CREATED", "Added interview round " + saved.getRoundName());
        return toDto(saved);
    }

    public InterviewRoundDto update(String username, Long applicationId, Long interviewId, UpsertInterviewRoundRequest request) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        InterviewRoundEntity entity = interviewRepository.findByIdAndApplication(interviewId, application)
                .orElseThrow(() -> new ResourceNotFoundException("InterviewRound", "id", interviewId));

        applyRequest(entity, request);
        entity.setUpdatedAt(Instant.now());

        InterviewRoundEntity saved = interviewRepository.save(entity);
        activityLogService.log(user, application, "INTERVIEW_UPDATED", "Updated interview round " + saved.getRoundName());
        return toDto(saved);
    }

    public void delete(String username, Long applicationId, Long interviewId) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        InterviewRoundEntity entity = interviewRepository.findByIdAndApplication(interviewId, application)
                .orElseThrow(() -> new ResourceNotFoundException("InterviewRound", "id", interviewId));

        interviewRepository.delete(entity);
        activityLogService.log(user, application, "INTERVIEW_DELETED", "Deleted interview round " + entity.getRoundName());
    }

    private void applyRequest(InterviewRoundEntity entity, UpsertInterviewRoundRequest request) {
        entity.setRoundName(request.getRoundName().trim());
        entity.setRoundType(request.getRoundType());
        entity.setScheduledAt(request.getScheduledAt());
        entity.setInterviewerName(request.getInterviewerName());
        entity.setNotes(request.getNotes());
        entity.setFeedback(request.getFeedback());
        entity.setResultStatus(request.getResultStatus());
    }

    private InterviewRoundDto toDto(InterviewRoundEntity entity) {
        InterviewRoundDto dto = new InterviewRoundDto();
        dto.setId(entity.getId());
        dto.setRoundName(entity.getRoundName());
        dto.setRoundType(entity.getRoundType());
        dto.setScheduledAt(entity.getScheduledAt());
        dto.setInterviewerName(entity.getInterviewerName());
        dto.setNotes(entity.getNotes());
        dto.setFeedback(entity.getFeedback());
        dto.setResultStatus(entity.getResultStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
