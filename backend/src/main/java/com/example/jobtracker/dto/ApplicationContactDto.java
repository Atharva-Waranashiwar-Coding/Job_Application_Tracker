package com.example.jobtracker.dto;

import java.time.Instant;
import java.time.LocalDate;

public class ApplicationContactDto {
    private Long id;
    private String recruiterName;
    private String recruiterEmail;
    private String referralContactName;
    private String referralContactEmail;
    private LocalDate outreachDate;
    private boolean referralRequested;
    private boolean referralReceived;
    private String followUpNotes;
    private Instant createdAt;
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public String getRecruiterEmail() {
        return recruiterEmail;
    }

    public void setRecruiterEmail(String recruiterEmail) {
        this.recruiterEmail = recruiterEmail;
    }

    public String getReferralContactName() {
        return referralContactName;
    }

    public void setReferralContactName(String referralContactName) {
        this.referralContactName = referralContactName;
    }

    public String getReferralContactEmail() {
        return referralContactEmail;
    }

    public void setReferralContactEmail(String referralContactEmail) {
        this.referralContactEmail = referralContactEmail;
    }

    public LocalDate getOutreachDate() {
        return outreachDate;
    }

    public void setOutreachDate(LocalDate outreachDate) {
        this.outreachDate = outreachDate;
    }

    public boolean isReferralRequested() {
        return referralRequested;
    }

    public void setReferralRequested(boolean referralRequested) {
        this.referralRequested = referralRequested;
    }

    public boolean isReferralReceived() {
        return referralReceived;
    }

    public void setReferralReceived(boolean referralReceived) {
        this.referralReceived = referralReceived;
    }

    public String getFollowUpNotes() {
        return followUpNotes;
    }

    public void setFollowUpNotes(String followUpNotes) {
        this.followUpNotes = followUpNotes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
