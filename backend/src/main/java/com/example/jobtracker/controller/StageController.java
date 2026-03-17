package com.example.jobtracker.controller;

import com.example.jobtracker.dto.ApplicationStageDto;
import com.example.jobtracker.dto.CreateStageRequest;
import com.example.jobtracker.service.StageService;
import com.example.jobtracker.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stages")
public class StageController {

    private final StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }

    @GetMapping
    public List<ApplicationStageDto> list(@RequestParam(required = false, defaultValue = "false") boolean includeArchived) {
        return stageService.listForUser(SecurityUtil.getCurrentUsername(), includeArchived);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationStageDto create(@Valid @RequestBody CreateStageRequest request) {
        return stageService.createForUser(SecurityUtil.getCurrentUsername(), request);
    }

    @PutMapping("/{id}")
    public ApplicationStageDto update(@PathVariable Long id, @Valid @RequestBody CreateStageRequest request) {
        return stageService.updateForUser(SecurityUtil.getCurrentUsername(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        stageService.archiveForUser(SecurityUtil.getCurrentUsername(), id);
    }
}
