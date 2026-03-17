package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ActivityLogEntity;
import com.example.jobtracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLogEntity, Long> {
    List<ActivityLogEntity> findByUser(UserEntity user);
}
