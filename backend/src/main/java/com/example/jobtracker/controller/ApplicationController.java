package com.example.jobtracker.controller;

import com.example.jobtracker.dto.ApplicationDto;
import com.example.jobtracker.dto.CreateApplicationRequest;
import com.example.jobtracker.dto.PagedResponse;
import com.example.jobtracker.dto.StageHistoryDto;
import com.example.jobtracker.dto.StageTransitionRequest;
import com.example.jobtracker.entity.enums.WorkMode;
import com.example.jobtracker.service.ApplicationService;
import com.example.jobtracker.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public PagedResponse<ApplicationDto> list(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(required = false) String q,
                                              @RequestParam(required = false) Long stageId,
                                              @RequestParam(required = false) String location,
                                              @RequestParam(required = false) WorkMode workMode,
                                              @RequestParam(required = false) Boolean sponsorship,
                                              @RequestParam(required = false) String source,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromAppliedAt,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toAppliedAt,
                                              @RequestParam(required = false, defaultValue = "STAGE_UPDATED") String sortBy,
                                              @RequestParam(required = false, defaultValue = "DESC") String sortDir,
                                              @RequestParam(required = false, defaultValue = "false") boolean includeDeleted) {
        return applicationService.listForUser(
                SecurityUtil.getCurrentUsername(),
                page,
                size,
                q,
                stageId,
                location,
                workMode,
                sponsorship,
                source,
                fromAppliedAt,
                toAppliedAt,
                sortBy,
                sortDir,
                includeDeleted
        );
    }

    @GetMapping("/{id}")
    public ApplicationDto get(@PathVariable Long id) {
        return applicationService.getForUser(SecurityUtil.getCurrentUsername(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationDto create(@Valid @RequestBody CreateApplicationRequest request) {
        return applicationService.createForUser(SecurityUtil.getCurrentUsername(), request);
    }

    @PutMapping("/{id}")
    public ApplicationDto update(@PathVariable Long id, @Valid @RequestBody CreateApplicationRequest request) {
        return applicationService.updateForUser(SecurityUtil.getCurrentUsername(), id, request);
    }

    @PatchMapping("/{id}/stage")
    public ApplicationDto moveStage(@PathVariable Long id, @Valid @RequestBody StageTransitionRequest request) {
        return applicationService.moveStageForUser(SecurityUtil.getCurrentUsername(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        applicationService.deleteForUser(SecurityUtil.getCurrentUsername(), id);
    }

    @PostMapping("/{id}/restore")
    public ApplicationDto restore(@PathVariable Long id) {
        return applicationService.restoreForUser(SecurityUtil.getCurrentUsername(), id);
    }

    @GetMapping("/{id}/stage-history")
    public List<StageHistoryDto> stageHistory(@PathVariable Long id) {
        return applicationService.stageHistoryForUser(SecurityUtil.getCurrentUsername(), id);
    }

    @GetMapping("/counts")
    public Map<String, Long> statusCounts() {
        return applicationService.statusCounts(SecurityUtil.getCurrentUsername());
    }
}
