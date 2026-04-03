package com.example.websecurity.security;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class TwoFactor {

    private final Map<String, String> otpStorage = new HashMap<>();
    private final Random random = new Random();

    // generiši 6-cifreni OTP
    public String generateOTP(String email) {
        int otpInt = 100000 + random.nextInt(900000);
        String otp = String.valueOf(otpInt);

        otpStorage.put(email, otp);
        System.out.println("Generated OTP for " + email + ": " + otp); // za test u konzoli
        return otp;
    }

    public boolean verifyOTP(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(email);
            return true;
        }
        return false;
    }
}