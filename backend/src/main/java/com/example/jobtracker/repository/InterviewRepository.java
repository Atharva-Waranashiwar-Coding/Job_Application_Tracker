package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.InterviewRoundEntity;
import com.example.jobtracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<InterviewRoundEntity, Long> {
    List<InterviewRoundEntity> findByApplicationOrderByScheduledAtAsc(ApplicationEntity application);

    Optional<InterviewRoundEntity> findByIdAndApplication(Long id, ApplicationEntity application);

    long countDistinctByApplication_UserAndApplication_DeletedAtIsNull(UserEntity user);
}
