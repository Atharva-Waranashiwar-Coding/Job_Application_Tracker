package com.example.jobtracker.dto;

import com.example.jobtracker.entity.enums.OfferDecisionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UpsertOfferRequest {

    private BigDecimal baseSalary;

    private BigDecimal bonus;

    private BigDecimal equity;

    @NotNull
    @Pattern(regexp = "[A-Z]{3}")
    private String currency = "USD";

    @Size(max = 255)
    private String location;

    private LocalDate offerDate;

    private LocalDate responseDeadline;

    @NotNull
    private OfferDecisionStatus decisionStatus;

    private String notes;

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    public BigDecimal getEquity() {
        return equity;
    }

    public void setEquity(BigDecimal equity) {
        this.equity = equity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(LocalDate offerDate) {
        this.offerDate = offerDate;
    }

    public LocalDate getResponseDeadline() {
        return responseDeadline;
    }

    public void setResponseDeadline(LocalDate responseDeadline) {
        this.responseDeadline = responseDeadline;
    }

    public OfferDecisionStatus getDecisionStatus() {
        return decisionStatus;
    }

    public void setDecisionStatus(OfferDecisionStatus decisionStatus) {
        this.decisionStatus = decisionStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
