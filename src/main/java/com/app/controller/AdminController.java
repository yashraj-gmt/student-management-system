package com.app.controller;

import com.app.entity.User;
import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;


    // Admin login page
    @GetMapping("/admin/login")
    public String adminLogin() {
        return "admin/admin_login";
    }

    // Admin dashboard page after login
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/admin_dashboard";
    }

//    public static String hashPassword(String plainPassword) {
//        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(plainPassword);
//    }


}
