package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ReminderEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.entity.enums.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<ReminderEntity, Long> {
    List<ReminderEntity> findByUserOrderByDueAtAsc(UserEntity user);

    List<ReminderEntity> findByUserAndStatusAndDueAtGreaterThanEqualOrderByDueAtAsc(UserEntity user, ReminderStatus status, Instant now);

    List<ReminderEntity> findByUserAndStatusAndDueAtLessThanOrderByDueAtAsc(UserEntity user, ReminderStatus status, Instant now);

    Optional<ReminderEntity> findByIdAndUser(Long id, UserEntity user);

    long countByUserAndStatusAndDueAtGreaterThanEqual(UserEntity user, ReminderStatus status, Instant now);

    long countByUserAndStatusAndDueAtLessThan(UserEntity user, ReminderStatus status, Instant now);
}
