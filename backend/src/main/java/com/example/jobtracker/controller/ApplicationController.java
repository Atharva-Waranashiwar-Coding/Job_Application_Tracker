package com.example.jobtracker.controller;

import com.example.jobtracker.dto.ApplicationDto;
import com.example.jobtracker.dto.CreateApplicationRequest;
import com.example.jobtracker.service.ApplicationService;
import com.example.jobtracker.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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
    public List<ApplicationDto> list() {
        return applicationService.listForUser(SecurityUtil.getCurrentUsername());
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        applicationService.deleteForUser(SecurityUtil.getCurrentUsername(), id);
    }

    @GetMapping("/counts")
    public Map<String, Long> statusCounts() {
        return applicationService.statusCounts(SecurityUtil.getCurrentUsername());
    }
}
