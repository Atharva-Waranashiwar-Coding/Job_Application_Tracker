package com.example.jobtracker.controller;

import com.example.jobtracker.dto.AuthResponse;
import com.example.jobtracker.dto.ForgotPasswordRequest;
import com.example.jobtracker.dto.LoginRequest;
import com.example.jobtracker.dto.RegisterRequest;
import com.example.jobtracker.dto.ResetPasswordRequest;
import com.example.jobtracker.dto.UserDto;
import com.example.jobtracker.security.JwtTokenProvider;
import com.example.jobtracker.security.LoginAttemptService;
import com.example.jobtracker.service.PasswordResetService;
import com.example.jobtracker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";

    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;

    public AuthController(UserService userService,
                          PasswordResetService passwordResetService,
                          JwtTokenProvider tokenProvider,
                          AuthenticationManager authenticationManager,
                          LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String rateLimitKey = buildRateLimitKey(request.getUsername(), httpRequest);

        if (loginAttemptService.isLocked(rateLimitKey)) {
            long retryAfterSeconds = loginAttemptService.secondsUntilUnlock(rateLimitKey);
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Too many failed login attempts. Try again in " + retryAfterSeconds + " seconds."
            );
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException ex) {
            loginAttemptService.recordFailure(rateLimitKey);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password.");
        }

        loginAttemptService.recordSuccess(rateLimitKey);

        Set<String> roles = Set.of(userService.getUserByUsername(request.getUsername()).getRole());
        String token = tokenProvider.createToken(request.getUsername(), roles);
        return new AuthResponse(token);
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return passwordResetService.createPasswordResetToken(request);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
    }

    private String buildRateLimitKey(String username, HttpServletRequest request) {
        String normalizedUsername = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
        String ip = extractClientIp(request);
        return normalizedUsername + "|" + ip;
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(FORWARDED_FOR_HEADER);
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
