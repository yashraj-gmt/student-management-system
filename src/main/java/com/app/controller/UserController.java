package com.app.controller;

import com.app.entity.Event;
import com.app.service.EmailService;
import com.app.service.EventService;
import com.app.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final EventService eventService;

    public UserController(UserService userService, EmailService emailService, EventService eventService) {
        this.userService = userService;
        this.emailService = emailService;
        this.eventService = eventService;
    }

//    @GetMapping("/home")
//    public String homePage() {
//        return "home";
//    }


    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("activePage", "about");
        return "public/about";
    }

    @GetMapping("/courses")
    public String coursesPage(Model model) {
        model.addAttribute("activePage", "courses");
        return "public/courses";
    }

//    @GetMapping("/contact")
//    public String contactPage(Model model) {
//        model.addAttribute("activePage", "contact");
//        return "public/contact";
//    }

//    @GetMapping("/index")
//    public String indexPage(Model model){
//        model.addAttribute("activePage", "index");
//        return "public/index";
//
//    }

    @GetMapping("/registerInfo")
    public String studentRegisterInfo(){
        return "register_info";
    }

    @GetMapping("/")
    public String rootPage(Model model) {
        model.addAttribute("activePage", "index");
        return "public/index";
    }


    @GetMapping("/gallery")
    public String eventGallery(@RequestParam(name = "categoryId", required = false) Long categoryId, Model model) {
        List<Event> events = (categoryId != null)
                ? eventService.getEventsByCategory(categoryId)
                : eventService.getAllEvents();
        model.addAttribute("categories", eventService.getAllCategories());
        model.addAttribute("events", events);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("activePage", "gallery");
        return "public/event_gallery";
    }

    @GetMapping("/event-details/{id}")
    public String viewParticularEvent(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            model.addAttribute("event", eventOpt.get());
            return "public/event_detail_view";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found.");
            return "redirect:/gallery";
        }
    }

//    @GetMapping("/register")
//    public String showRegisterForm(Model model) {
//        model.addAttribute("user", new User());
//        return "ABCregister";
//    }

   /* @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model) {
        // Check if email is already registered
        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        // Check if password and confirmPassword match
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        // Save user (password should be encoded in the service layer)
        userService.saveUser(user);
        return "redirect:/login?success";
    }*/

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "email", required = false) String email,
                            Model model) {
        if (error != null) {
            if ("wrong-password".equals(error)) {
                model.addAttribute("error", "Incorrect password. Try again.");
            } else if ("user-not-found".equals(error)) {
                model.addAttribute("error", "Email not registered.");
            } else {
                model.addAttribute("error", "Login failed due to unknown error.");
            }
        }
        model.addAttribute("email", email);
        return "login";
    }


/*
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm(@RequestParam(value = "email", required = false) String email, Model model) {
        if (email != null) {
            model.addAttribute("email", email);
        }
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
        long expiryTime = System.currentTimeMillis() + (1 * 60 * 1000);
        userService.saveOtp(user, otp, expiryTime);
        emailService.sendOtpEmail(user.getEmail(), otp);

        userService.generateAndSendOtp(user);
        model.addAttribute("email", email);
//        model.addAttribute("otpExpiry", user.getOtpExpiry());////updates
        return "verify_otp";
    }

    @PostMapping("/save-login-email")
    @ResponseBody
    public void saveLoginEmail(@RequestParam String email, HttpSession session) {
        session.setAttribute("emailForLogin", email);
    }

    @GetMapping("/verify-otp")
    public String showOtpForm(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "verify_otp"; // OTP verification page
    }

//*
///updates
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



    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String email, Model model) {
        model.addAttribute("email", email); // Pass email to the reset form
        return "reset_password"; // Show reset password page
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String email,
                                       @RequestParam String newPassword,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "Invalid request.");
            return "reset_password"; // Invalid user or session
        }
        redirectAttributes.addFlashAttribute("message", "Password reset successfully. Please login.");
        userService.updatePassword(user, newPassword); // Update the password
        return "redirect:/login?resetSuccess"; // Redirect to login page with success message
    }*/



}
