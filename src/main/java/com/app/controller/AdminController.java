package com.app.controller;

import com.app.entity.StandardWiseStudentCount;
import com.app.repository.UserRepository;
import com.app.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentService studentService;

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

    @GetMapping("/admin/logout")
    public String adminLogout() {
        return "admin/admin_login";
    }

    @GetMapping("/api/student-count-by-standard")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public List<StandardWiseStudentCount> getStudentCountByStandard() {
        return studentService.fetchStandardWiseStudentCount();
    }

//    public static String hashPassword(String plainPassword) {
//        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(plainPassword);
//    }


}
