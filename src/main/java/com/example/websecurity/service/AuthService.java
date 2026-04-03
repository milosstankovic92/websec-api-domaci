package com.example.websecurity.service;

import com.example.websecurity.exception.WebSecMissingDataException;
import com.example.websecurity.persistence.User;
import com.example.websecurity.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // --- brute-force zaštita ---
    private final Map<String, Integer> attempts = new HashMap<>();
    private final Map<String, Long> blockedUntil = new HashMap<>();

    private final int MAX_ATTEMPTS = 3;        // maksimalni neuspešni pokušaji
    private final long BLOCK_TIME = 10_000;    // blokada 10 sekundi (za test)

    // --- autentifikacija korisnika ---
    public User authenticate(String email, String password) {
        if (isBlocked(email)) {
            throw new WebSecMissingDataException("Too many attempts. Try again later.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    loginFailed(email);
                    return new WebSecMissingDataException("User not found");
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            loginFailed(email);
            throw new WebSecMissingDataException("Invalid password");
        }

        // uspešan login
        loginSucceeded(email);
        return user;
    }

    // --- pomoćne metode za brute-force ---
    private boolean isBlocked(String email) {
        if (!blockedUntil.containsKey(email)) return false;

        long unblockTime = blockedUntil.get(email);
        if (System.currentTimeMillis() > unblockTime) {
            // blokada je istekla
            blockedUntil.remove(email);
            attempts.remove(email);
            return false;
        }
        return true; // još uvek je blokiran
    }

    private void loginSucceeded(String email) {
        attempts.remove(email);
        blockedUntil.remove(email);
    }

    private void loginFailed(String email) {
        int currentAttempts = attempts.getOrDefault(email, 0);
        currentAttempts++;
        attempts.put(email, currentAttempts);

        if (currentAttempts >= MAX_ATTEMPTS) {
            blockedUntil.put(email, System.currentTimeMillis() + BLOCK_TIME);
        }
    }
}