package com.example.jobtracker.dto;

import java.time.Instant;

public class StageHistoryDto {
    private Long id;
    private Long fromStageId;
    private String fromStageName;
    private Long toStageId;
    private String toStageName;
    private Instant changedAt;
    private String reason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromStageId() {
        return fromStageId;
    }

    public void setFromStageId(Long fromStageId) {
        this.fromStageId = fromStageId;
    }

    public String getFromStageName() {
        return fromStageName;
    }

    public void setFromStageName(String fromStageName) {
        this.fromStageName = fromStageName;
    }

    public Long getToStageId() {
        return toStageId;
    }

    public void setToStageId(Long toStageId) {
        this.toStageId = toStageId;
    }

    public String getToStageName() {
        return toStageName;
    }

    public void setToStageName(String toStageName) {
        this.toStageName = toStageName;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
