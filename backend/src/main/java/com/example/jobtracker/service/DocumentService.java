package com.example.jobtracker.service;

import com.example.jobtracker.dto.ApplicationDocumentsDto;
import com.example.jobtracker.dto.UpdateApplicationDocumentsRequest;
import com.example.jobtracker.entity.ApplicationDocumentEntity;
import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ApplicationAccessService applicationAccessService;
    private final ActivityLogService activityLogService;

    public DocumentService(DocumentRepository documentRepository,
                           ApplicationAccessService applicationAccessService,
                           ActivityLogService activityLogService) {
        this.documentRepository = documentRepository;
        this.applicationAccessService = applicationAccessService;
        this.activityLogService = activityLogService;
    }

    public ApplicationDocumentsDto getForApplication(String username, Long applicationId) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        return documentRepository.findByApplication(application)
                .map(this::toDto)
                .orElseGet(() -> emptyDto(applicationId));
    }

    public ApplicationDocumentsDto updateForApplication(String username,
                                                        Long applicationId,
                                                        UpdateApplicationDocumentsRequest request) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        ApplicationDocumentEntity entity = documentRepository.findByApplication(application)
                .orElseGet(() -> {
                    ApplicationDocumentEntity fresh = new ApplicationDocumentEntity();
                    fresh.setApplication(application);
                    return fresh;
                });

        entity.setResumeVersion(request.getResumeVersion());
        entity.setCoverLetterVersion(request.getCoverLetterVersion());
        entity.setResumeRef(request.getResumeRef());
        entity.setCoverLetterRef(request.getCoverLetterRef());
        entity.setPortfolioRef(request.getPortfolioRef());
        entity.setGithubUrl(request.getGithubUrl());
        entity.setLinkedinUrl(request.getLinkedinUrl());
        entity.setUpdatedAt(Instant.now());

        ApplicationDocumentEntity saved = documentRepository.save(entity);
        activityLogService.log(user, application, "DOCUMENTS_UPDATED", "Updated document references for application " + applicationId);
        return toDto(saved);
    }

    private ApplicationDocumentsDto emptyDto(Long applicationId) {
        ApplicationDocumentsDto dto = new ApplicationDocumentsDto();
        dto.setApplicationId(applicationId);
        return dto;
    }

    private ApplicationDocumentsDto toDto(ApplicationDocumentEntity entity) {
        ApplicationDocumentsDto dto = new ApplicationDocumentsDto();
        dto.setId(entity.getId());
        dto.setApplicationId(entity.getApplication().getId());
        dto.setResumeVersion(entity.getResumeVersion());
        dto.setCoverLetterVersion(entity.getCoverLetterVersion());
        dto.setResumeRef(entity.getResumeRef());
        dto.setCoverLetterRef(entity.getCoverLetterRef());
        dto.setPortfolioRef(entity.getPortfolioRef());
        dto.setGithubUrl(entity.getGithubUrl());
        dto.setLinkedinUrl(entity.getLinkedinUrl());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
