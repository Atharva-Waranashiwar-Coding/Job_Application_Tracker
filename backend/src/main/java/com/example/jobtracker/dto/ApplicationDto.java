package com.example.jobtracker.dto;

import com.example.jobtracker.entity.enums.ApplicationPriority;
import com.example.jobtracker.entity.enums.EmploymentType;
import com.example.jobtracker.entity.enums.WorkMode;

import java.time.Instant;
import java.time.LocalDate;

public class ApplicationDto {
    private Long id;
    private String company;
    private String role;
    private ApplicationStageDto stage;
    private Instant stageEnteredAt;
    private long daysInCurrentStage;
    private boolean stalled;
    private ApplicationPriority priority;
    private LocalDate appliedAt;
    private String sourceUrl;
    private String notes;
    private Instant updatedAt;

    private String location;
    private WorkMode workMode;
    private EmploymentType employmentType;
    private String salaryRange;
    private Boolean sponsorshipAvailable;
    private String applicationSource;
    private LocalDate applicationDeadline;
    private String postingUrl;
    private String jobDescription;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public ApplicationStageDto getStage() {
        return stage;
    }

    public void setStage(ApplicationStageDto stage) {
        this.stage = stage;
    }

    public Instant getStageEnteredAt() {
        return stageEnteredAt;
    }

    public void setStageEnteredAt(Instant stageEnteredAt) {
        this.stageEnteredAt = stageEnteredAt;
    }

    public long getDaysInCurrentStage() {
        return daysInCurrentStage;
    }

    public void setDaysInCurrentStage(long daysInCurrentStage) {
        this.daysInCurrentStage = daysInCurrentStage;
    }

    public boolean isStalled() {
        return stalled;
    }

    public void setStalled(boolean stalled) {
        this.stalled = stalled;
    }

    public ApplicationPriority getPriority() {
        return priority;
    }

    public void setPriority(ApplicationPriority priority) {
        this.priority = priority;
    }

    public LocalDate getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDate appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public WorkMode getWorkMode() {
        return workMode;
    }

    public void setWorkMode(WorkMode workMode) {
        this.workMode = workMode;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public Boolean getSponsorshipAvailable() {
        return sponsorshipAvailable;
    }

    public void setSponsorshipAvailable(Boolean sponsorshipAvailable) {
        this.sponsorshipAvailable = sponsorshipAvailable;
    }

    public String getApplicationSource() {
        return applicationSource;
    }

    public void setApplicationSource(String applicationSource) {
        this.applicationSource = applicationSource;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public String getPostingUrl() {
        return postingUrl;
    }

    public void setPostingUrl(String postingUrl) {
        this.postingUrl = postingUrl;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}
