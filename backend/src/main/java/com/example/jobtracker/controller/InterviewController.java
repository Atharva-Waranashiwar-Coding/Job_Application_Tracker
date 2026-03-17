package com.example.jobtracker.controller;

import com.example.jobtracker.dto.InterviewRoundDto;
import com.example.jobtracker.dto.UpsertInterviewRoundRequest;
import com.example.jobtracker.service.InterviewService;
import com.example.jobtracker.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications/{applicationId}/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping
    public List<InterviewRoundDto> list(@PathVariable Long applicationId) {
        return interviewService.listForApplication(SecurityUtil.getCurrentUsername(), applicationId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewRoundDto create(@PathVariable Long applicationId,
                                    @Valid @RequestBody UpsertInterviewRoundRequest request) {
        return interviewService.create(SecurityUtil.getCurrentUsername(), applicationId, request);
    }

    @PutMapping("/{interviewId}")
    public InterviewRoundDto update(@PathVariable Long applicationId,
                                    @PathVariable Long interviewId,
                                    @Valid @RequestBody UpsertInterviewRoundRequest request) {
        return interviewService.update(SecurityUtil.getCurrentUsername(), applicationId, interviewId, request);
    }

    @DeleteMapping("/{interviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long applicationId, @PathVariable Long interviewId) {
        interviewService.delete(SecurityUtil.getCurrentUsername(), applicationId, interviewId);
    }
}
