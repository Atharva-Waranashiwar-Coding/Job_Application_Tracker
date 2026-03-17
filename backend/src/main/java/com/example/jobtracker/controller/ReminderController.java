package com.example.jobtracker.controller;

import com.example.jobtracker.dto.ReminderDto;
import com.example.jobtracker.dto.UpsertReminderRequest;
import com.example.jobtracker.service.ReminderService;
import com.example.jobtracker.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping
    public List<ReminderDto> list() {
        return reminderService.list(SecurityUtil.getCurrentUsername());
    }

    @GetMapping("/upcoming")
    public List<ReminderDto> upcoming() {
        return reminderService.upcoming(SecurityUtil.getCurrentUsername());
    }

    @GetMapping("/overdue")
    public List<ReminderDto> overdue() {
        return reminderService.overdue(SecurityUtil.getCurrentUsername());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderDto create(@Valid @RequestBody UpsertReminderRequest request) {
        return reminderService.create(SecurityUtil.getCurrentUsername(), request);
    }

    @PutMapping("/{id}")
    public ReminderDto update(@PathVariable Long id, @Valid @RequestBody UpsertReminderRequest request) {
        return reminderService.update(SecurityUtil.getCurrentUsername(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reminderService.delete(SecurityUtil.getCurrentUsername(), id);
    }
}
