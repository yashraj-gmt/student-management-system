package com.app.service.impl;

import com.app.entity.Student;
import com.app.entity.User;
import com.app.repository.StudentRepository;
import com.app.repository.UserRepository;
import com.app.service.EmailService;
import com.app.service.StudentRegistrationService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class StudentRegistrationServiceImpl implements StudentRegistrationService {

    private final StudentRepository studentRepo;
    private final UserRepository userRepo;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final StudentServiceImpl studentServiceImpl;


    public StudentRegistrationServiceImpl(StudentRepository studentRepo, UserRepository userRepo,
                                          EmailService emailService, BCryptPasswordEncoder encoder, StudentServiceImpl studentServiceImpl) {
        this.studentRepo = studentRepo;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.passwordEncoder = encoder;
        this.studentServiceImpl = studentServiceImpl;
    }

    @Override
    public void preRegisterStudent(Student student) {

        if (student == null || student.getUserAccount() == null) {
            throw new IllegalArgumentException("Student or associated User account cannot be null");
        }

        // Generate enrollment number and set to student
//        String enrollmentNumber = generateEnrollmentNumber();
//        student.setEnrollmentNumber(enrollmentNumber);
        student.setEnrollmentNumber(studentServiceImpl.generateEnrollmentNumber());

        String randomPassword = generateRandomPassword(8);
        String token = generateToken();

        User user = student.getUserAccount();
        user.setPassword(passwordEncoder.encode(randomPassword));
        user.setTemporaryPassword(passwordEncoder.encode(randomPassword)); // store hashed temp password
        user.setRegistrationToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(24));
        user.setPasswordUpdated(false);
        userRepo.save(user);

        student.setOtp(null);
        student.setOtpExpiry(null);
        studentRepo.save(student);

        emailService.sendAccountCreationEmail(user.getEmail(), student.getFirstName(), randomPassword, token);
    }

    private String generateToken() {
        return java.util.UUID.randomUUID().toString();
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        // OTP no longer used; disable or remove this if not needed
        return false;
    }

    @Override
    public void setPassword(String email, String newPassword) {
        Optional<User> optionalUser = userRepo.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setTemporaryPassword(null);
            user.setRegistrationToken(null);
            user.setTokenExpiry(null);
            user.setPasswordUpdated(true);
            userRepo.save(user);
        }
    }


    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for(int i=0; i<length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public boolean validateGeneratedPassword(String email, String token, String generatedPassword) {
        Optional<User> optionalUser = userRepo.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        if (user.getRegistrationToken() == null
                || !user.getRegistrationToken().equals(token)
                || user.getTokenExpiry() == null
                || user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            return false; // token invalid or expired
        }

        return passwordEncoder.matches(generatedPassword, user.getTemporaryPassword());
    }


}
