package com.example.websecurity.api;

import com.example.websecurity.persistence.User;
import com.example.websecurity.security.TwoFactor;
import com.example.websecurity.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TwoFactor twoFactorService;

    // 1. login sa lozinkom
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        try {
            User user = authService.authenticate(email, password); // koristi postojeći AuthService
            twoFactorService.generateOTP(email); // OTP ide u konzolu
            return ResponseEntity.ok(Map.of("message", "OTP sent! Check console."));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    // 2. proveri OTP
    @PostMapping("/login/otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (twoFactorService.verifyOTP(email, otp)) {
            return ResponseEntity.ok(Map.of("message", "Login successful!"));
        } else {
            return ResponseEntity.status(401).body("Invalid OTP");
        }
    }
}