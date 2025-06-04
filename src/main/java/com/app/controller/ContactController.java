package com.app.controller;

import com.app.entity.ContactMessage;
import com.app.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContactController {

    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("contactMessage", new ContactMessage());
        return "/public/contact"; // Thymeleaf template (contact.html)
    }

    @PostMapping("/contact/send")
    public String sendContactMessage(@ModelAttribute ContactMessage contactMessage, Model model) {
        String to = "yashraj.gmtechnosys@gmail.com"; // Change to your desired recipient
        String subject = "New Contact Form Submission: " + contactMessage.getSubject();
        String body = "From: " + contactMessage.getName() + " <" + contactMessage.getEmail() + ">\n\n"
                + contactMessage.getMessage();

        emailService.sendEmail(to, subject, body);
        return "redirect:/contact?success";
    }
}
