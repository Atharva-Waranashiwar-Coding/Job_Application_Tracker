package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.ApplicationStageEntity;
import com.example.jobtracker.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long>, JpaSpecificationExecutor<ApplicationEntity> {
    List<ApplicationEntity> findByUserAndDeletedAtIsNull(UserEntity user);

    Optional<ApplicationEntity> findByIdAndUserAndDeletedAtIsNull(Long id, UserEntity user);

    Optional<ApplicationEntity> findByIdAndUser(Long id, UserEntity user);

    Page<ApplicationEntity> findByUserAndDeletedAtIsNull(UserEntity user, Pageable pageable);

    Page<ApplicationEntity> findByUserAndDeletedAtIsNotNull(UserEntity user, Pageable pageable);

    long countByUserAndDeletedAtIsNull(UserEntity user);

    long countByUserAndDeletedAtIsNullAndStage_TerminalRejectionTrue(UserEntity user);

    long countByUserAndDeletedAtIsNullAndStage_TerminalSuccessTrue(UserEntity user);

    long countByUserAndDeletedAtIsNullAndStage(UserEntity user, ApplicationStageEntity stage);
}
