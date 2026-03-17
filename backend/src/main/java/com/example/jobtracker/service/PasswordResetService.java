package com.example.jobtracker.service;

import com.example.jobtracker.dto.ForgotPasswordRequest;
import com.example.jobtracker.dto.ResetPasswordRequest;
import com.example.jobtracker.entity.PasswordResetTokenEntity;
import com.example.jobtracker.repository.PasswordResetTokenRepository;
import com.example.jobtracker.util.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserService userService;
    private final long tokenExpirationMillis;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UserService userService,
                                @Value("${password.reset.token-expiration-ms:3600000}") long tokenExpirationMillis) {
        this.tokenRepository = tokenRepository;
        this.userService = userService;
        this.tokenExpirationMillis = tokenExpirationMillis;
    }

    public String createPasswordResetToken(ForgotPasswordRequest request) {
        String username = request.getUsername();
        userService.getUserByUsername(username); // validate user exists

        PasswordResetTokenEntity token = new PasswordResetTokenEntity();
        token.setToken(UUID.randomUUID().toString());
        token.setUsername(username);
        token.setExpiresAt(Instant.now().plusMillis(tokenExpirationMillis));
        token.setUsed(false);

        PasswordResetTokenEntity saved = tokenRepository.save(token);
        return saved.getToken();
    }

    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetTokenEntity token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("PasswordResetToken", "token", request.getToken()));

        if (token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Reset token is invalid or expired.");
        }

        userService.updatePassword(token.getUsername(), request.getNewPassword());

        token.setUsed(true);
        tokenRepository.save(token);
    }
}
