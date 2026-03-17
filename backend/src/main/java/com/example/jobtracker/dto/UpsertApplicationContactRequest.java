package com.example.jobtracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UpsertApplicationContactRequest {

    @Size(max = 200)
    private String recruiterName;

    @Email
    @Size(max = 255)
    private String recruiterEmail;

    @Size(max = 200)
    private String referralContactName;

    @Email
    @Size(max = 255)
    private String referralContactEmail;

    private LocalDate outreachDate;

    private boolean referralRequested;

    private boolean referralReceived;

    private String followUpNotes;

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
}
