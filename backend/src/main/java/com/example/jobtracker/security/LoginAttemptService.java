package com.example.jobtracker.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class LoginAttemptService {

    private final ConcurrentMap<String, AttemptState> attempts = new ConcurrentHashMap<>();
    private final int maxAttempts;
    private final long windowMillis;
    private final long lockoutMillis;

    public LoginAttemptService(
            @Value("${app.security.login.max-attempts:5}") int maxAttempts,
            @Value("${app.security.login.window-ms:600000}") long windowMillis,
            @Value("${app.security.login.lockout-ms:900000}") long lockoutMillis
    ) {
        this.maxAttempts = Math.max(1, maxAttempts);
        this.windowMillis = Math.max(1000, windowMillis);
        this.lockoutMillis = Math.max(1000, lockoutMillis);
    }

    public boolean isLocked(String key) {
        AttemptState state = attempts.get(key);
        if (state == null) {
            return false;
        }

        Instant now = Instant.now();
        if (state.lockedUntil() != null && state.lockedUntil().isAfter(now)) {
            return true;
        }

        if (state.windowStart().plusMillis(windowMillis).isBefore(now)
                || (state.lockedUntil() != null && !state.lockedUntil().isAfter(now))) {
            attempts.remove(key, state);
        }

        return false;
    }

    public long secondsUntilUnlock(String key) {
        AttemptState state = attempts.get(key);
        if (state == null || state.lockedUntil() == null) {
            return 0;
        }

        long seconds = Duration.between(Instant.now(), state.lockedUntil()).getSeconds();
        return Math.max(0, seconds);
    }

    public void recordFailure(String key) {
        Instant now = Instant.now();
        attempts.compute(key, (ignored, state) -> {
            AttemptState current = state;

            if (current == null || current.windowStart().plusMillis(windowMillis).isBefore(now)) {
                current = new AttemptState(0, now, null);
            }

            if (current.lockedUntil() != null && current.lockedUntil().isAfter(now)) {
                return current;
            }

            int failures = current.failures() + 1;
            Instant lockedUntil = failures >= maxAttempts ? now.plusMillis(lockoutMillis) : null;
            return new AttemptState(failures, current.windowStart(), lockedUntil);
        });
    }

    public void recordSuccess(String key) {
        attempts.remove(key);
    }

    private record AttemptState(int failures, Instant windowStart, Instant lockedUntil) {
    }
}
