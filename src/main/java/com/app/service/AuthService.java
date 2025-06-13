package com.app.service;

import com.app.entity.User;
import com.app.exception.AccountLockedException;
import com.app.exception.InvalidEmailException;
import com.app.exception.InvalidPasswordException;
import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCK_TIME_DURATION = 24; // hours

    public boolean authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return new BCryptPasswordEncoder().matches(password, user.getPassword());
        }
        return false;
    }

    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void authenticateWithValidation(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidEmailException("Email is not registered"));

        if (user.isAccountLocked()) {
            if (user.getLockTime() != null) {
                LocalDateTime unlockTime = user.getLockTime().plusHours(LOCK_TIME_DURATION);
                if (LocalDateTime.now().isBefore(unlockTime)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a");
                    String formattedUnlockTime = unlockTime.format(formatter);
                    throw new AccountLockedException("Account locked. Try again after " + formattedUnlockTime);
                } else {
                    user.setAccountLocked(false);
                    user.setFailedAttempts(0);
                    user.setLockTime(null);
                    userRepository.save(user);
                }
            }
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            int newFailAttempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(newFailAttempts);
            if (newFailAttempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountLocked(true);
                user.setLockTime(LocalDateTime.now());
                userRepository.save(user);
                throw new AccountLockedException("Account locked due to multiple failed attempts. Try again after 24 hours.");
            }
            userRepository.save(user);
            int attemptsLeft = MAX_FAILED_ATTEMPTS - newFailAttempts;
            throw new InvalidPasswordException("Wrong password. You have " + attemptsLeft + " attempt(s) remaining.");
        }

        // successful login
        user.setFailedAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);
        userRepository.save(user);
    }
}
