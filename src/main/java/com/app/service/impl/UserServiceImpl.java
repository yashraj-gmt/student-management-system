package com.app.service.impl;

import com.app.entity.User;
import com.app.repository.UserRepository;
import com.app.service.EmailService;
import com.app.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, EmailService emailService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

//    @Override
//    public User findByResetToken(String token) {
//        // You can remove this if you're not using token-based reset anymore
//        return userRepository.findByResetToken(token);
//    }

    @Override
    public void updatePassword(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }

    @Override
    public void generateAndSendOtp(User user) {
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        long expiryTime = System.currentTimeMillis() + (1 * 60 * 1000);
        user.setOtp(otp);
        user.setOtpExpiry(expiryTime);
        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }


    @Override
    public void saveOtp(User user, String otp, long expiryTime) {
        user.setOtp(otp);
        user.setOtpExpiry(expiryTime);
        userRepository.save(user);
    }
}
