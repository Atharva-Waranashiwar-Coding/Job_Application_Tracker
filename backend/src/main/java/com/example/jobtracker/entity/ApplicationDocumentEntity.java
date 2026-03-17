package com.example.jobtracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "application_documents")
public class ApplicationDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private ApplicationEntity application;

    @Column(name = "resume_version", length = 100)
    private String resumeVersion;

    @Column(name = "cover_letter_version", length = 100)
    private String coverLetterVersion;

    @Column(name = "resume_ref", length = 1000)
    private String resumeRef;

    @Column(name = "cover_letter_ref", length = 1000)
    private String coverLetterRef;

    @Column(name = "portfolio_ref", length = 1000)
    private String portfolioRef;

    @Column(name = "github_url", length = 1000)
    private String githubUrl;

    @Column(name = "linkedin_url", length = 1000)
    private String linkedinUrl;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationEntity getApplication() {
        return application;
    }

    public void setApplication(ApplicationEntity application) {
        this.application = application;
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
