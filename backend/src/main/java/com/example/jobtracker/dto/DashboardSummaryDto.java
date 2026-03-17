package com.example.jobtracker.dto;

import java.util.List;

public class DashboardSummaryDto {
    private long totalApplications;
    private List<StageCountDto> applicationsByStage;
    private double responseRate;
    private double interviewConversionRate;
    private long offerCount;
    private long rejectionCount;
    private long upcomingReminders;
    private long overdueReminders;

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public List<StageCountDto> getApplicationsByStage() {
        return applicationsByStage;
    }

    public void setApplicationsByStage(List<StageCountDto> applicationsByStage) {
        this.applicationsByStage = applicationsByStage;
    }

    public double getResponseRate() {
        return responseRate;
    }

    public void setResponseRate(double responseRate) {
        this.responseRate = responseRate;
    }

    public double getInterviewConversionRate() {
        return interviewConversionRate;
    }

    public void setInterviewConversionRate(double interviewConversionRate) {
        this.interviewConversionRate = interviewConversionRate;
    }

    public long getOfferCount() {
        return offerCount;
    }

    public void setOfferCount(long offerCount) {
        this.offerCount = offerCount;
    }

    public long getRejectionCount() {
        return rejectionCount;
    }

    public void setRejectionCount(long rejectionCount) {
        this.rejectionCount = rejectionCount;
    }

    public long getUpcomingReminders() {
        return upcomingReminders;
    }

    public void setUpcomingReminders(long upcomingReminders) {
        this.upcomingReminders = upcomingReminders;
    }

    public long getOverdueReminders() {
        return overdueReminders;
    }

    public void setOverdueReminders(long overdueReminders) {
        this.overdueReminders = overdueReminders;
    }
}
