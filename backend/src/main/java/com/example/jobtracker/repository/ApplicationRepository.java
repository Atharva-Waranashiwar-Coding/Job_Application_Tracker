package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
    List<ApplicationEntity> findByUser(UserEntity user);
}
