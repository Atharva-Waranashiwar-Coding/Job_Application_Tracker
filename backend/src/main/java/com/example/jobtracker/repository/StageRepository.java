package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ApplicationStageEntity;
import com.example.jobtracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StageRepository extends JpaRepository<ApplicationStageEntity, Long> {
    List<ApplicationStageEntity> findByUserOrderByDisplayOrderAsc(UserEntity user);

    List<ApplicationStageEntity> findByUserAndArchivedFalseOrderByDisplayOrderAsc(UserEntity user);

    Optional<ApplicationStageEntity> findByIdAndUser(Long id, UserEntity user);

    Optional<ApplicationStageEntity> findByUserAndNameIgnoreCase(UserEntity user, String name);

    boolean existsByUserAndNameIgnoreCase(UserEntity user, String name);

    boolean existsByUserAndNameIgnoreCaseAndIdNot(UserEntity user, String name, Long id);

    Optional<ApplicationStageEntity> findFirstByUserAndArchivedFalseOrderByDisplayOrderAsc(UserEntity user);

    @Query("select coalesce(max(s.displayOrder), 0) from ApplicationStageEntity s where s.user = :user")
    int findMaxDisplayOrderByUser(UserEntity user);
}
