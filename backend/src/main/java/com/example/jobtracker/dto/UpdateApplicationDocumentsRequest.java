package com.example.jobtracker.dto;

import jakarta.validation.constraints.Size;

public class UpdateApplicationDocumentsRequest {

    @Size(max = 100)
    private String resumeVersion;

    @Size(max = 100)
    private String coverLetterVersion;

    @Size(max = 1000)
    private String resumeRef;

    @Size(max = 1000)
    private String coverLetterRef;

    @Size(max = 1000)
    private String portfolioRef;

    @Size(max = 1000)
    private String githubUrl;

    @Size(max = 1000)
    private String linkedinUrl;

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
}
