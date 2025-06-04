package com.app.service;

import com.app.entity.Student;

public interface RegistrationService {

    void preRegisterStudent(Student student);

    boolean verifyOtpAndSetPassword(String otp, String password);
}
