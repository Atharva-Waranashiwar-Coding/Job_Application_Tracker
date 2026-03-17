package com.example.jobtracker.dto;

public class ApplicationStageDto {
    private Long id;
    private String name;
    private Integer displayOrder;
    private String colorHex;
    private boolean terminalRejection;
    private boolean terminalSuccess;
    private boolean archived;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public boolean isTerminalRejection() {
        return terminalRejection;
    }

    public void setTerminalRejection(boolean terminalRejection) {
        this.terminalRejection = terminalRejection;
    }

    public boolean isTerminalSuccess() {
        return terminalSuccess;
    }

    public void setTerminalSuccess(boolean terminalSuccess) {
        this.terminalSuccess = terminalSuccess;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
