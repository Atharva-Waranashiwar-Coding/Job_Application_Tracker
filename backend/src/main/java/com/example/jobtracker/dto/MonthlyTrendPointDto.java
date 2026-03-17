package com.example.jobtracker.dto;

public class MonthlyTrendPointDto {
    private String month;
    private long applications;
    private long rejections;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public long getApplications() {
        return applications;
    }

    public void setApplications(long applications) {
        this.applications = applications;
    }

    public long getRejections() {
        return rejections;
    }

    public void setRejections(long rejections) {
        this.rejections = rejections;
    }
}
