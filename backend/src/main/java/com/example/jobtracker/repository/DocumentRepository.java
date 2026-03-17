package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ApplicationDocumentEntity;
import com.example.jobtracker.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<ApplicationDocumentEntity, Long> {
    Optional<ApplicationDocumentEntity> findByApplication(ApplicationEntity application);
}
