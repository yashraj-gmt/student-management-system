package com.app.controller;

import com.app.entity.Student;
import com.app.entity.User;
import com.app.service.AuthService;
import com.app.service.StandardService;
import com.app.service.StudentRegistrationService;
import com.app.service.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    private final StudentRegistrationService registrationService;
    private final AuthService authService;
    private final StandardService standardService;
    private final UserDetailsService userDetailsService;

    public RegistrationController(StudentRegistrationService registrationService, AuthService authService, StandardService standardService,UserDetailsService userDetailsService) {
        this.registrationService = registrationService;
        this.authService = authService;
        this.standardService = standardService;
        this.userDetailsService = userDetailsService;
    }


    @GetMapping("/admin/students/pre-register")
    public String preRegisterForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("standards", standardService.getAllStandards());
        return "admin/pre_register_student";
    }

    @PostMapping("/admin/student/pre-register")
    public String handlePreRegister(@ModelAttribute Student student, Model model) {
        if (authService.isEmailRegistered(student.getEmail())) {
            model.addAttribute("error", "Email is already registered.");
            model.addAttribute("student", student);
            model.addAttribute("standards", standardService.getAllStandards());
            return "admin/pre_register_student";
        }

        User user = new User();
        user.setEmail(student.getEmail());
        student.setUserAccount(user);

        registrationService.preRegisterStudent(student);
        model.addAttribute("message", "Registration link sent to student!");
        model.addAttribute("student", new Student());
        model.addAttribute("standards", standardService.getAllStandards());
        return "admin/pre_register_student";
    }

    @GetMapping("/admin/student/check-email")
    @ResponseBody
    public boolean checkEmailExists(@RequestParam String email) {
        return authService.isEmailRegistered(email); // returns true if email exists
    }

    @GetMapping("/complete-registration")
    public String completeRegistration(@RequestParam String email, @RequestParam String token, Model model) {
        model.addAttribute("email", email);
        model.addAttribute("token", token);
        return "registration/verify_password"; // new Thymeleaf page
    }

    @PostMapping("/complete-registration")
    public String verifyGeneratedPassword(@RequestParam String email,
                                          @RequestParam String token,
                                          @RequestParam String generatedPassword,
                                          @RequestParam String password,
                                          @RequestParam String confirmPassword,
                                          Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("email", email);
            model.addAttribute("token", token);
            return "registration/verify_password";
        }

        if (!registrationService.validateGeneratedPassword(email, token, generatedPassword)) {
            model.addAttribute("error", "Generated password is incorrect");
            model.addAttribute("email", email);
            model.addAttribute("token", token);
            return "registration/verify_password";
        }

        registrationService.setPassword(email, password);
        return "redirect:/user-login?verified=true";
    }

    @GetMapping("/user-login")
    public String showLoginPage(@RequestParam(value = "email", required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "login";
    }

    @PostMapping("/user-login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        try {
            boolean authenticated = authService.authenticate(email, password);
            if (authenticated) {
                // Create an Authentication object and set it into SecurityContext
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);

                return "redirect:/students/home";
            } else {
                model.addAttribute("error", "Invalid email or password");
                model.addAttribute("email", email);
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("email", email);
            return "login";
        }
    }



}