package com.countyassembly.caims.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION = 15 * 60 * 1000; // 15 minutes

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> lockCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        lockCache.remove(username);
    }

    public void loginFailed(String username) {
        if (username == null) return;

        int attempts = attemptsCache.getOrDefault(username, 0);
        attempts++;
        attemptsCache.put(username, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            lockCache.put(username, System.currentTimeMillis() + LOCK_DURATION);
        }
    }

    public boolean isBlocked(String username) {
        if (username == null) return false;

        Long lockTime = lockCache.get(username);
        if (lockTime == null) {
            return false;
        }

        // Check if lock has expired
        if (System.currentTimeMillis() > lockTime) {
            // Unlock
            lockCache.remove(username);
            attemptsCache.remove(username);
            return false;
        }

        return true;
    }

    public int getRemainingAttempts(String username) {
        if (username == null) return MAX_ATTEMPTS;
        int attempts = attemptsCache.getOrDefault(username, 0);
        return Math.max(0, MAX_ATTEMPTS - attempts);
    }
}