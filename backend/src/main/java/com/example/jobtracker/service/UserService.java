package com.example.jobtracker.service;

import com.example.jobtracker.dto.CreateUserRequest;
import com.example.jobtracker.dto.RegisterRequest;
import com.example.jobtracker.dto.UserDto;
import com.example.jobtracker.entity.UserEntity;
import com.example.jobtracker.repository.UserRepository;
import com.example.jobtracker.util.ResourceAlreadyExistsException;
import com.example.jobtracker.util.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StageService stageService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       StageService stageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.stageService = stageService;
    }

    public List<UserDto> listUsers() {
        return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public UserDto getUser(Long id) {
        return userRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    public void updatePassword(String username, String newPassword) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("User", "username", request.getUsername());
        }

        UserEntity entity = new UserEntity();
        entity.setUsername(request.getUsername());
        entity.setDisplayName(request.getDisplayName());
        entity.setRole(request.getRole());
        entity.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        UserEntity saved = userRepository.save(entity);
        stageService.createDefaultStagesForUser(saved);
        return toDto(saved);
    }

    public UserDto registerUser(RegisterRequest request) {
        CreateUserRequest createRequest = new CreateUserRequest();
        createRequest.setUsername(request.getUsername());
        createRequest.setPassword(request.getPassword());
        createRequest.setDisplayName(request.getDisplayName());
        createRequest.setRole("USER");
        return createUser(createRequest);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    private UserDto toDto(UserEntity entity) {
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setDisplayName(entity.getDisplayName());
        dto.setRole(entity.getRole());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
