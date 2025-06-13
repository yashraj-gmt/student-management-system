package com.app.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Generic method to send any custom email
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    // Sends reset password email with a link
    public void sendResetPasswordEmail(String to, String resetLink) {
        String subject = "Reset Your Password";
        String body = "Click the following link to reset your password:\n\n"
                + resetLink + "\n\n"
                + "If you didn't request this, please ignore this email.\n\n"
                + "Regards,\nStudent Management System Team";
        sendEmail(to, subject, body);
    }

    public void sendAccountCreationEmail(String to, String studentName, String randomPassword, String token) {
        String subject = "Complete Your Registration - Student Management System";

        // Add both email and token to link
        String verificationLink = "http://localhost:8080/complete-registration?email=" + to
                + "&token=" + token;

        String body = "Dear " + studentName + ",\n\n"
                + "Your student account has been created successfully.\n"
                + "Your temporary password is: " + randomPassword + "\n\n"
                + "To complete your registration and set a permanent password, please visit the following link:\n"
                + verificationLink + "\n\n"
                + "This link is valid for a limited time. Please do not share your password or link with anyone.\n\n"
                + "Best regards,\n"
                + "Student Management System Team";
        sendEmail(to, subject, body);
    }

    public void sendWelcomeEmailWithPassword(String to, String studentName, String plainPassword) {
        String subject = "Welcome to Student Portal - Login Details";
        String body = "Dear " + studentName + ",\n\n"
                + "Your student account has been created successfully.\n"
                + "You can log in to the portal using the following credentials:\n\n"
                + "Email: " + to + "\n"
                + "Password: " + plainPassword + "\n\n"
                + "We recommend changing your password after first login.\n\n"
                + "Best regards,\n"
                + "Student Management System Team";

        sendEmail(to, subject, body);
    }



}
