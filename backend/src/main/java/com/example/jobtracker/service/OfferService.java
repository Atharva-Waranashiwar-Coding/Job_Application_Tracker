package com.example.jobtracker.service;

import com.example.jobtracker.dto.OfferDto;
import com.example.jobtracker.dto.UpsertOfferRequest;
import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.OfferEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.repository.OfferRepository;
import com.example.jobtracker.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OfferService {

    private final OfferRepository offerRepository;
    private final ApplicationAccessService applicationAccessService;
    private final ActivityLogService activityLogService;

    public OfferService(OfferRepository offerRepository,
                        ApplicationAccessService applicationAccessService,
                        ActivityLogService activityLogService) {
        this.offerRepository = offerRepository;
        this.applicationAccessService = applicationAccessService;
        this.activityLogService = activityLogService;
    }

    public List<OfferDto> listForApplication(String username, Long applicationId) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        return offerRepository.findByApplicationOrderByOfferDateDesc(application)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public OfferDto create(String username, Long applicationId, UpsertOfferRequest request) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        OfferEntity entity = new OfferEntity();
        entity.setApplication(application);
        applyRequest(entity, request);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        OfferEntity saved = offerRepository.save(entity);
        activityLogService.log(user, application, "OFFER_CREATED", "Added offer for application " + applicationId);
        return toDto(saved);
    }

    public OfferDto update(String username, Long applicationId, Long offerId, UpsertOfferRequest request) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        OfferEntity entity = offerRepository.findByIdAndApplication(offerId, application)
                .orElseThrow(() -> new ResourceNotFoundException("Offer", "id", offerId));

        applyRequest(entity, request);
        entity.setUpdatedAt(Instant.now());

        OfferEntity saved = offerRepository.save(entity);
        activityLogService.log(user, application, "OFFER_UPDATED", "Updated offer " + offerId);
        return toDto(saved);
    }

    public void delete(String username, Long applicationId, Long offerId) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationEntity application = applicationAccessService.findActiveApplication(user, applicationId);

        OfferEntity entity = offerRepository.findByIdAndApplication(offerId, application)
                .orElseThrow(() -> new ResourceNotFoundException("Offer", "id", offerId));

        offerRepository.delete(entity);
        activityLogService.log(user, application, "OFFER_DELETED", "Deleted offer " + offerId);
    }

    private void applyRequest(OfferEntity entity, UpsertOfferRequest request) {
        validateNonNegative(request.getBaseSalary(), "baseSalary");
        validateNonNegative(request.getBonus(), "bonus");
        validateNonNegative(request.getEquity(), "equity");

        entity.setBaseSalary(request.getBaseSalary());
        entity.setBonus(request.getBonus());
        entity.setEquity(request.getEquity());
        entity.setCurrency(request.getCurrency());
        entity.setLocation(request.getLocation());
        entity.setOfferDate(request.getOfferDate());
        entity.setResponseDeadline(request.getResponseDeadline());
        entity.setDecisionStatus(request.getDecisionStatus());
        entity.setNotes(request.getNotes());
    }

    private void validateNonNegative(BigDecimal value, String fieldName) {
        if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " must be non-negative.");
        }
    }

    private OfferDto toDto(OfferEntity entity) {
        OfferDto dto = new OfferDto();
        dto.setId(entity.getId());
        dto.setBaseSalary(entity.getBaseSalary());
        dto.setBonus(entity.getBonus());
        dto.setEquity(entity.getEquity());
        dto.setCurrency(entity.getCurrency());
        dto.setLocation(entity.getLocation());
        dto.setOfferDate(entity.getOfferDate());
        dto.setResponseDeadline(entity.getResponseDeadline());
        dto.setDecisionStatus(entity.getDecisionStatus());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
