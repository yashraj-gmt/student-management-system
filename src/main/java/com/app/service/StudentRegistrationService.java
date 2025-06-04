package com.app.service;

import com.app.entity.Student;
import com.app.entity.User;

public interface StudentRegistrationService {
    void preRegisterStudent(Student student);
    boolean validateOtp(String email, String otp);
    void setPassword(String email, String newPassword);
    public boolean validateGeneratedPassword(String email, String token, String generatedPassword);
}
