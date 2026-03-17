package com.example.jobtracker.dto;

import com.example.jobtracker.entity.enums.NoteCategory;
import jakarta.validation.constraints.NotNull;

public class UpsertApplicationNoteRequest {

    @NotNull
    private NoteCategory category;

    private String content;

    public NoteCategory getCategory() {
        return category;
    }

    public void setCategory(NoteCategory category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
