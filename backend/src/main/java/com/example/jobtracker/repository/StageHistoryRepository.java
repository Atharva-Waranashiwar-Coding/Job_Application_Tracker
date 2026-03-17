package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.ApplicationStageHistoryEntity;
import com.example.jobtracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StageHistoryRepository extends JpaRepository<ApplicationStageHistoryEntity, Long> {
    List<ApplicationStageHistoryEntity> findByApplicationOrderByChangedAtDesc(ApplicationEntity application);

    List<ApplicationStageHistoryEntity> findByApplication_UserOrderByChangedAtAsc(UserEntity user);
}
