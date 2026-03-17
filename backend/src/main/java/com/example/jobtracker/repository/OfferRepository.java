package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.OfferEntity;
import com.example.jobtracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<OfferEntity, Long> {
    List<OfferEntity> findByApplicationOrderByOfferDateDesc(ApplicationEntity application);

    Optional<OfferEntity> findByIdAndApplication(Long id, ApplicationEntity application);

    long countByApplication_UserAndApplication_DeletedAtIsNull(UserEntity user);
}
