package com.app.service;

import com.app.entity.User;
import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository; // or your UserService

    public boolean authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Compare stored password hash with provided password
            // For example, if you use BCrypt:
            return new BCryptPasswordEncoder().matches(password, user.getPassword());
        }
        return false;
    }

    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();  // assumes you have findByEmail
    }

}
