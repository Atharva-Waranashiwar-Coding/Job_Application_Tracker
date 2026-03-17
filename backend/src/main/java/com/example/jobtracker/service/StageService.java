package com.example.jobtracker.service;

import com.example.jobtracker.dto.ApplicationStageDto;
import com.example.jobtracker.dto.CreateStageRequest;
import com.example.jobtracker.entity.ApplicationStageEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.repository.ApplicationRepository;
import com.example.jobtracker.repository.StageRepository;
import com.example.jobtracker.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class StageService {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#?[A-Fa-f0-9]{6}$");

    private final StageRepository stageRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationAccessService applicationAccessService;

    public StageService(StageRepository stageRepository,
                        ApplicationRepository applicationRepository,
                        ApplicationAccessService applicationAccessService) {
        this.stageRepository = stageRepository;
        this.applicationRepository = applicationRepository;
        this.applicationAccessService = applicationAccessService;
    }

    public List<ApplicationStageDto> listForUser(String username, boolean includeArchived) {
        UserEntity user = applicationAccessService.findUser(username);
        return listForUser(user, includeArchived).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ApplicationStageEntity> listForUser(UserEntity user, boolean includeArchived) {
        if (includeArchived) {
            return stageRepository.findByUserOrderByDisplayOrderAsc(user);
        }
        return stageRepository.findByUserAndArchivedFalseOrderByDisplayOrderAsc(user);
    }

    public ApplicationStageDto createForUser(String username, CreateStageRequest request) {
        UserEntity user = applicationAccessService.findUser(username);
        validateStageRequest(user, request, null);

        ApplicationStageEntity stage = new ApplicationStageEntity();
        stage.setUser(user);
        stage.setName(request.getName().trim());
        stage.setDisplayOrder(resolveDisplayOrder(user, request.getDisplayOrder()));
        stage.setColorHex(normalizeColor(request.getColorHex()));
        stage.setTerminalRejection(request.isTerminalRejection());
        stage.setTerminalSuccess(request.isTerminalSuccess());
        stage.setArchived(request.isArchived());
        stage.setCreatedAt(Instant.now());
        stage.setUpdatedAt(Instant.now());

        return toDto(stageRepository.save(stage));
    }

    public ApplicationStageDto updateForUser(String username, Long id, CreateStageRequest request) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationStageEntity stage = stageRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationStage", "id", id));

        validateStageRequest(user, request, stage.getId());

        stage.setName(request.getName().trim());
        stage.setDisplayOrder(resolveDisplayOrder(user, request.getDisplayOrder()));
        stage.setColorHex(normalizeColor(request.getColorHex()));
        stage.setTerminalRejection(request.isTerminalRejection());
        stage.setTerminalSuccess(request.isTerminalSuccess());
        stage.setArchived(request.isArchived());
        stage.setUpdatedAt(Instant.now());

        return toDto(stageRepository.save(stage));
    }

    public void archiveForUser(String username, Long id) {
        UserEntity user = applicationAccessService.findUser(username);
        ApplicationStageEntity stage = stageRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationStage", "id", id));

        long activeApplications = applicationRepository.countByUserAndDeletedAtIsNullAndStage(user, stage);
        if (activeApplications > 0) {
            throw new IllegalStateException("Cannot archive a stage that is currently assigned to active applications.");
        }

        stage.setArchived(true);
        stage.setUpdatedAt(Instant.now());
        stageRepository.save(stage);
    }

    public ApplicationStageEntity getStageForUser(UserEntity user, Long stageId) {
        ApplicationStageEntity stage = stageRepository.findByIdAndUser(stageId, user)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationStage", "id", stageId));
        if (stage.isArchived()) {
            throw new IllegalStateException("Cannot use an archived stage.");
        }
        return stage;
    }

    public ApplicationStageEntity getDefaultStageForUser(UserEntity user) {
        return stageRepository.findFirstByUserAndArchivedFalseOrderByDisplayOrderAsc(user)
                .orElseThrow(() -> new IllegalStateException("No active stages configured for this user."));
    }

    public void createDefaultStagesForUser(UserEntity user) {
        ensureDefaultStage(user, "Applied", 1, "#0F6DFF", false, false);
        ensureDefaultStage(user, "Interviewing", 2, "#7A58FF", false, false);
        ensureDefaultStage(user, "Offer", 3, "#0F9F82", false, true);
        ensureDefaultStage(user, "Rejected", 4, "#DB4A63", true, false);
        ensureDefaultStage(user, "Hired", 5, "#F2A838", false, true);
    }

    private void ensureDefaultStage(UserEntity user,
                                    String name,
                                    int displayOrder,
                                    String colorHex,
                                    boolean terminalRejection,
                                    boolean terminalSuccess) {
        if (stageRepository.existsByUserAndNameIgnoreCase(user, name)) {
            return;
        }

        ApplicationStageEntity stage = new ApplicationStageEntity();
        stage.setUser(user);
        stage.setName(name);
        stage.setDisplayOrder(displayOrder);
        stage.setColorHex(colorHex);
        stage.setTerminalRejection(terminalRejection);
        stage.setTerminalSuccess(terminalSuccess);
        stage.setArchived(false);
        stage.setCreatedAt(Instant.now());
        stage.setUpdatedAt(Instant.now());
        stageRepository.save(stage);
    }

    private void validateStageRequest(UserEntity user, CreateStageRequest request, Long existingStageId) {
        if (request.isTerminalSuccess() && request.isTerminalRejection()) {
            throw new IllegalArgumentException("A stage cannot be both terminal success and terminal rejection.");
        }

        boolean duplicateName = existingStageId == null
                ? stageRepository.existsByUserAndNameIgnoreCase(user, request.getName().trim())
                : stageRepository.existsByUserAndNameIgnoreCaseAndIdNot(user, request.getName().trim(), existingStageId);
        if (duplicateName) {
            throw new IllegalStateException("Stage name must be unique per user.");
        }

        if (!HEX_COLOR_PATTERN.matcher(request.getColorHex()).matches()) {
            throw new IllegalArgumentException("colorHex must be a valid six-digit hex color.");
        }
    }

    private int resolveDisplayOrder(UserEntity user, Integer requestedOrder) {
        if (requestedOrder != null && requestedOrder >= 0) {
            return requestedOrder;
        }
        return stageRepository.findMaxDisplayOrderByUser(user) + 1;
    }

    private String normalizeColor(String colorHex) {
        String normalized = colorHex.trim();
        return normalized.startsWith("#") ? normalized.toUpperCase() : ("#" + normalized.toUpperCase());
    }

    public ApplicationStageDto toDto(ApplicationStageEntity entity) {
        ApplicationStageDto dto = new ApplicationStageDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setColorHex(entity.getColorHex());
        dto.setTerminalRejection(entity.isTerminalRejection());
        dto.setTerminalSuccess(entity.isTerminalSuccess());
        dto.setArchived(entity.isArchived());
        return dto;
    }
}
