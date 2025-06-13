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
        return "/public/contact";
    }

    @PostMapping("/contact/send")
    public String sendContactMessage(@ModelAttribute ContactMessage contactMessage, Model model) {
        String adminEmail = "yashraj.gmtechnosys@gmail.com";  // your internal email

        // Send to Admin
        String subject = "New Contact Form Submission: " + contactMessage.getSubject();
        String body = "From: " + contactMessage.getName() + " <" + contactMessage.getEmail() + ">\n\n"
                + contactMessage.getMessage();
        emailService.sendEmail(adminEmail, subject, body);

        // Send Revert Mail to User
        sendAutoReply(contactMessage);

        return "redirect:/contact?success";
    }

    private void sendAutoReply(ContactMessage contactMessage) {
        String subject = "Thank you for contacting Student Management System";
        String body = "Dear " + contactMessage.getName() + ",\n\n"
                + "We have received your inquiry. Our team will get back to you as soon as possible.\n\n"
                + "Regards,\nStudent Management System Team";
        emailService.sendEmail(contactMessage.getEmail(), subject, body);
    }
}
