package com.app.service.impl;

import com.app.entity.Student;
import com.app.entity.User;
import com.app.repository.StudentRepository;
import com.app.service.EmailService;
import com.app.service.RegistrationService;
import com.app.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final StudentRepository studentRepo;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String DIGIT = "0123456789";
    private static final String PASSWORD_ALLOW = CHAR_LOWER + CHAR_UPPER + DIGIT;
    private static final int PASSWORD_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();

    public RegistrationServiceImpl(StudentRepository studentRepo, UserService userService,
                                   EmailService emailService, PasswordEncoder passwordEncoder) {
        this.studentRepo = studentRepo;
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    private String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int rndCharAt = random.nextInt(PASSWORD_ALLOW.length());
            char rndChar = PASSWORD_ALLOW.charAt(rndCharAt);
            sb.append(rndChar);
        }
        return sb.toString();
    }

    @Override
    public void preRegisterStudent(Student student) {
        String randomPassword = generateRandomPassword();

        User user = student.getUserAccount();
        user.setPassword(passwordEncoder.encode(randomPassword));
        user.setTemporaryPassword(randomPassword);
        user.setPasswordUpdated(false);
        userService.save(user);

        // Remove OTP related fields if present
        student.setOtp(null);
        student.setOtpExpiry(null);
        studentRepo.save(student);

        String subject = "Your Account Registration Password";
        String body = "Dear " + student.getFirstName() + ",\n\n"
                + "Your account has been created successfully.\n"
                + "Your temporary password is: " + randomPassword + "\n"
                + "Please change it after first login.\n\n"
                + "Regards,\nAdmin Team";

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public boolean verifyOtpAndSetPassword(String otp, String newPassword) {
        // Since OTP is removed, you can either remove this method
        // or modify it to just allow password update after login.

        // For now, just return false or throw UnsupportedOperationException
        throw new UnsupportedOperationException("OTP verification is disabled. Use password reset instead.");
    }

}
