package com.example.jobtracker.dto;

import java.util.List;

public class DashboardTrendsDto {
    private List<MonthlyTrendPointDto> monthly;
    private List<RejectionByStageDto> rejectionsByStage;

    public List<MonthlyTrendPointDto> getMonthly() {
        return monthly;
    }

    public void setMonthly(List<MonthlyTrendPointDto> monthly) {
        this.monthly = monthly;
    }

    public List<RejectionByStageDto> getRejectionsByStage() {
        return rejectionsByStage;
    }

    public void setRejectionsByStage(List<RejectionByStageDto> rejectionsByStage) {
        this.rejectionsByStage = rejectionsByStage;
    }
}
