package com.app.controller;

import com.app.entity.Subject;
import com.app.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    // Display all subjects
    @GetMapping
    public String listSubjects(Model model) {
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "subject_list";
    }

    // Add new subject form
    @GetMapping("/new")
    public String showSubjectForm(Model model) {
        model.addAttribute("subject", new Subject());
        return "subject_form";
    }

    // Save new subject
    @PostMapping("/save")
    public String saveSubject(@ModelAttribute Subject subject) {
        subjectService.saveSubject(subject);
        return "redirect:/subjects";
    }

    // Delete subject
    @GetMapping("/delete/{id}")
    public String deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubjectById(id);
        return "redirect:/subjects";
    }
}
