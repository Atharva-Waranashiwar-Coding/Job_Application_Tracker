package com.example.jobtracker.service;

import com.example.jobtracker.entity.ApplicationEntity;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.repository.ApplicationRepository;
import com.example.jobtracker.repository.UserRepository;
import com.example.jobtracker.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationAccessService {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public ApplicationAccessService(UserRepository userRepository, ApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    public UserEntity findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    public ApplicationEntity findApplication(UserEntity user, Long applicationId) {
        return applicationRepository.findByIdAndUser(applicationId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));
    }

    public ApplicationEntity findActiveApplication(UserEntity user, Long applicationId) {
        return applicationRepository.findByIdAndUserAndDeletedAtIsNull(applicationId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));
    }

    public void ensureApplicationEditable(ApplicationEntity application) {
        if (application.getDeletedAt() != null) {
            throw new IllegalStateException("Cannot modify a soft-deleted application.");
        }
    }
}
