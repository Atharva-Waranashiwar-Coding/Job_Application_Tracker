package com.example.jobtracker.service;

import com.example.jobtracker.dto.ApplicationNoteDto;
import com.example.jobtracker.dto.UpsertApplicationNoteRequest;
import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.ApplicationNoteEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final ApplicationAccessService applicationAccessService;
    private final ActivityLogService activityLogService;

    public NoteService(NoteRepository noteRepository,
                       ApplicationAccessService applicationAccessService,
                       ActivityLogService activityLogService) {
        this.noteRepository = noteRepository;
        this.applicationAccessService = applicationAccessService;
        this.activityLogService = activityLogService;
    }

    public List<ApplicationNoteDto> listForApplication(String username, Long applicationId) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        return noteRepository.findByApplicationOrderByCategoryAsc(application)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ApplicationNoteDto upsert(String username, Long applicationId, UpsertApplicationNoteRequest request) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        ApplicationNoteEntity entity = noteRepository.findByApplicationAndCategory(application, request.getCategory())
                .orElseGet(() -> {
                    ApplicationNoteEntity fresh = new ApplicationNoteEntity();
                    fresh.setApplication(application);
                    fresh.setCategory(request.getCategory());
                    return fresh;
                });

        entity.setContent(request.getContent());
        entity.setUpdatedAt(Instant.now());

        ApplicationNoteEntity saved = noteRepository.save(entity);
        activityLogService.log(user, application, "NOTE_UPDATED", "Updated " + request.getCategory() + " note");
        return toDto(saved);
    }

    private ApplicationNoteDto toDto(ApplicationNoteEntity entity) {
        ApplicationNoteDto dto = new ApplicationNoteDto();
        dto.setId(entity.getId());
        dto.setCategory(entity.getCategory());
        dto.setContent(entity.getContent());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
