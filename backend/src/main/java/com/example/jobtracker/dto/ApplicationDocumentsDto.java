package com.example.jobtracker.dto;

import java.time.Instant;

public class ApplicationDocumentsDto {
    private Long id;
    private Long applicationId;
    private String resumeVersion;
    private String coverLetterVersion;
    private String resumeRef;
    private String coverLetterRef;
    private String portfolioRef;
    private String githubUrl;
    private String linkedinUrl;
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getResumeVersion() {
        return resumeVersion;
    }

    public void setResumeVersion(String resumeVersion) {
        this.resumeVersion = resumeVersion;
    }

    public String getCoverLetterVersion() {
        return coverLetterVersion;
    }

    public void setCoverLetterVersion(String coverLetterVersion) {
        this.coverLetterVersion = coverLetterVersion;
    }

    public String getResumeRef() {
        return resumeRef;
    }

    public void setResumeRef(String resumeRef) {
        this.resumeRef = resumeRef;
    }

    public String getCoverLetterRef() {
        return coverLetterRef;
    }

    public void setCoverLetterRef(String coverLetterRef) {
        this.coverLetterRef = coverLetterRef;
    }

    public String getPortfolioRef() {
        return portfolioRef;
    }

    public void setPortfolioRef(String portfolioRef) {
        this.portfolioRef = portfolioRef;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
