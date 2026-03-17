package com.example.jobtracker.controller;

import com.example.jobtracker.dto.DashboardSummaryDto;
import com.example.jobtracker.dto.DashboardTrendsDto;
import com.example.jobtracker.service.DashboardService;
import com.example.jobtracker.util.SecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummaryDto summary() {
        return dashboardService.summary(SecurityUtil.getCurrentUsername());
    }

    @GetMapping("/trends")
    public DashboardTrendsDto trends(@RequestParam(defaultValue = "12") int months) {
        return dashboardService.trends(SecurityUtil.getCurrentUsername(), months);
    }
}
