package com.app.controller;

import com.app.entity.User;
import com.app.service.EmailService;
import com.app.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }
        userService.saveUser(user);  // Password is encoded in service
        return "redirect:/login?success";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";  // Spring Security handles actual login POST
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Optional if using Spring Security logout
        return "redirect:/login?logout";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "No account found.");
            return "forgot_password";
        }

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        long expiryTime = System.currentTimeMillis() + (1 * 60 * 1000);

        userService.saveOtp(user, otp, expiryTime);
        emailService.sendOtpEmail(user.getEmail(), otp);

        model.addAttribute("email", email);
        model.addAttribute("otpExpiry", expiryTime);
        return "verify_otp";
    }

    @GetMapping("/verify-otp")
    public String showOtpForm(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "verify_otp"; // OTP verification page
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp,
                            Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "Invalid request.");
            return "verify_otp";
        }

        if (user.getOtpExpiry() < System.currentTimeMillis()) {
            model.addAttribute("error", "OTP has expired!");
            model.addAttribute("email", email);
            model.addAttribute("otpExpiry", user.getOtpExpiry());
            return "verify_otp";
        }

        if (!otp.equals(user.getOtp())) {
            model.addAttribute("error", "Invalid OTP");
            model.addAttribute("email", email);
            model.addAttribute("otpExpiry", user.getOtpExpiry());
            return "verify_otp";
        }

        // clears otp
        user.setOtp(null);
        user.setOtpExpiry(0L);
        userService.saveUser(user);  // clear OTP

        // Move to password reset
        model.addAttribute("email", email);
        return "reset_password";
    }


    /*@PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp,
                            Model model) {
        User user = userService.findByEmail(email);
        if (user == null || user.getOtp() == null || !user.getOtp().equals(otp)) {
            model.addAttribute("error", "Invalid OTP");
            return "verify_otp"; // If OTP is invalid, stay on the OTP verification page
        }

        if (user.getOtpExpiry() < System.currentTimeMillis()) {
            model.addAttribute("error", "OTP has expired");
            return "verify_otp"; // If OTP expired, stay on the OTP verification page
        }

        // OTP verified successfully, now redirect to reset password page
        model.addAttribute("email", email);  // Pass email to reset password page
        return "reset_password";  // This should point to your reset_password.html page
    }*/



    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String email, Model model) {
        model.addAttribute("email", email); // Pass email to the reset form
        return "reset_password"; // Show reset password page
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String email,
                                       @RequestParam String newPassword,
                                       Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "Invalid request.");
            return "reset_password"; // Invalid user or session
        }

        userService.updatePassword(user, newPassword); // Update the password
        return "redirect:/login?resetSuccess"; // Redirect to login page with success message
    }
}

    /*@GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "No account found.");
            return "forgot_password";
        }

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userService.saveUser(user);

        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendResetPasswordEmail(user.getEmail(), resetLink);

        model.addAttribute("message", "Reset link sent to your email.");
        return "forgot_password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String newPassword,
                                       Model model) {
        User user = userService.findByResetToken(token);
        if (user == null) {
            model.addAttribute("error", "Invalid token");
            return "reset_password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userService.saveUser(user);

        return "redirect:/login?resetSuccess";
    }


*/