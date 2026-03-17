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
import java.time.LocalDate;

@Entity
@Table(name = "application_contacts")
public class ApplicationContactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private ApplicationEntity application;

    @Column(name = "recruiter_name", length = 200)
    private String recruiterName;

    @Column(name = "recruiter_email", length = 255)
    private String recruiterEmail;

    @Column(name = "referral_contact_name", length = 200)
    private String referralContactName;

    @Column(name = "referral_contact_email", length = 255)
    private String referralContactEmail;

    @Column(name = "outreach_date")
    private LocalDate outreachDate;

    @Column(name = "referral_requested", nullable = false)
    private boolean referralRequested;

    @Column(name = "referral_received", nullable = false)
    private boolean referralReceived;

    @Column(name = "follow_up_notes", columnDefinition = "text")
    private String followUpNotes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

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
