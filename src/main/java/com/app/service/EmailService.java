/*
package com.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


    private final JavaMailSender mailSender;

    public  EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

   */
/* public void sendResetPasswordEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset your password");
        message.setText("Click the link to reset your password: " + resetLink);
        mailSender.send(message);
    }*//*


    */
/*public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp);
        mailSender.send(message);
    }
*//*


    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your One-Time Password (OTP) for Password Reset");

        String emailBody = "Dear User,\n\n"
                + "We received a request to reset your password. Please use the following One-Time Password (OTP) to proceed:\n\n"
                + "🔐 OTP: " + otp + "\n\n"
                + "This OTP is valid for the next 1 minutes. Do not share this code with anyone.\n\n"
                + "If you did not request a password reset, please ignore this email.\n\n"
                + "Best regards,\n"
                + "Student Management System Team";

        message.setText(emailBody);
        mailSender.send(message);
    }

}*/
