package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.ApplicationNoteEntity;
import com.example.jobtracker.entity.enums.NoteCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<ApplicationNoteEntity, Long> {
    List<ApplicationNoteEntity> findByApplicationOrderByCategoryAsc(ApplicationEntity application);

    Optional<ApplicationNoteEntity> findByApplicationAndCategory(ApplicationEntity application, NoteCategory category);
}
