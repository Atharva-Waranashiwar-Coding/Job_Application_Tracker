package com.example.jobtracker.service;

import com.example.jobtracker.dto.ApplicationContactDto;
import com.example.jobtracker.dto.UpsertApplicationContactRequest;
import com.example.jobtracker.entity.ApplicationContactEntity;
import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.repository.ContactRepository;
import com.example.jobtracker.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;
    private final ApplicationAccessService applicationAccessService;
    private final ActivityLogService activityLogService;

    public ContactService(ContactRepository contactRepository,
                          ApplicationAccessService applicationAccessService,
                          ActivityLogService activityLogService) {
        this.contactRepository = contactRepository;
        this.applicationAccessService = applicationAccessService;
        this.activityLogService = activityLogService;
    }

    public List<ApplicationContactDto> listForApplication(String username, Long applicationId) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        return contactRepository.findByApplicationOrderByCreatedAtDesc(application)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ApplicationContactDto create(String username, Long applicationId, UpsertApplicationContactRequest request) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        ApplicationContactEntity entity = new ApplicationContactEntity();
        entity.setApplication(application);
        applyRequest(entity, request);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        ApplicationContactEntity saved = contactRepository.save(entity);
        activityLogService.log(user, application, "CONTACT_CREATED", "Added networking contact");
        return toDto(saved);
    }

    public ApplicationContactDto update(String username,
                                        Long applicationId,
                                        Long contactId,
                                        UpsertApplicationContactRequest request) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        ApplicationContactEntity entity = contactRepository.findByIdAndApplication(contactId, application)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationContact", "id", contactId));

        applyRequest(entity, request);
        entity.setUpdatedAt(Instant.now());

        ApplicationContactEntity saved = contactRepository.save(entity);
        activityLogService.log(user, application, "CONTACT_UPDATED", "Updated networking contact " + contactId);
        return toDto(saved);
    }

    public void delete(String username, Long applicationId, Long contactId) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        ApplicationContactEntity entity = contactRepository.findByIdAndApplication(contactId, application)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationContact", "id", contactId));

        contactRepository.delete(entity);
        activityLogService.log(user, application, "CONTACT_DELETED", "Deleted networking contact " + contactId);
    }

    private void applyRequest(ApplicationContactEntity entity, UpsertApplicationContactRequest request) {
        entity.setRecruiterName(request.getRecruiterName());
        entity.setRecruiterEmail(request.getRecruiterEmail());
        entity.setReferralContactName(request.getReferralContactName());
        entity.setReferralContactEmail(request.getReferralContactEmail());
        entity.setOutreachDate(request.getOutreachDate());
        entity.setReferralRequested(request.isReferralRequested());
        entity.setReferralReceived(request.isReferralReceived());
        entity.setFollowUpNotes(request.getFollowUpNotes());
    }

    private ApplicationContactDto toDto(ApplicationContactEntity entity) {
        ApplicationContactDto dto = new ApplicationContactDto();
        dto.setId(entity.getId());
        dto.setRecruiterName(entity.getRecruiterName());
        dto.setRecruiterEmail(entity.getRecruiterEmail());
        dto.setReferralContactName(entity.getReferralContactName());
        dto.setReferralContactEmail(entity.getReferralContactEmail());
        dto.setOutreachDate(entity.getOutreachDate());
        dto.setReferralRequested(entity.isReferralRequested());
        dto.setReferralReceived(entity.isReferralReceived());
        dto.setFollowUpNotes(entity.getFollowUpNotes());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
