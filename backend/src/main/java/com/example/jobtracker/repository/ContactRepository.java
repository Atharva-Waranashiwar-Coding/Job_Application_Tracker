package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ApplicationContactEntity;
import com.example.jobtracker.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<ApplicationContactEntity, Long> {
    List<ApplicationContactEntity> findByApplicationOrderByCreatedAtDesc(ApplicationEntity application);

    Optional<ApplicationContactEntity> findByIdAndApplication(Long id, ApplicationEntity application);
}
