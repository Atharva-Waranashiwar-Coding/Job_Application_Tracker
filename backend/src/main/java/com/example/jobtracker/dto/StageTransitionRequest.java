package com.example.jobtracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StageTransitionRequest {

    @NotNull
    private Long toStageId;

    @Size(max = 255)
    private String reason;

    public Long getToStageId() {
        return toStageId;
    }

    public void setToStageId(Long toStageId) {
        this.toStageId = toStageId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
