package com.example.jobtracker.controller;

import com.example.jobtracker.dto.ActivityLogDto;
import com.example.jobtracker.service.ActivityLogService;
import com.example.jobtracker.service.UserService;
import com.example.jobtracker.util.SecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final UserService userService;

    public ActivityLogController(ActivityLogService activityLogService, UserService userService) {
        this.activityLogService = activityLogService;
        this.userService = userService;
    }

    @GetMapping
    public List<ActivityLogDto> list() {
        // Activity log is scoped to current user
        String username = SecurityUtil.getCurrentUsername();
        return activityLogService.listForUser(userService.getUserByUsername(username));
    }
}
