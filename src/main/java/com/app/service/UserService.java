package com.app.service;

import com.app.entity.User;

public interface UserService {

    void saveUser(User user);

    User findByEmail(String email);

//    User findByResetToken(String token); // Optional if you no longer use token-based reset

    void updatePassword(User user, String rawPassword);

    void saveOtp(User user, String otp, long expiryTime);

    void generateAndSendOtp(User user);
}
