package com.app.service.impl;

import com.app.entity.User;
import com.app.repository.UserRepository;
import com.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
    public void saveOtp(User user, String otp, long expiryTime) {
        user.setOtp(otp);
        user.setOtpExpiry(expiryTime);
        userRepository.save(user);
    }
}
