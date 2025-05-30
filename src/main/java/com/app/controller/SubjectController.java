package com.app.controller;

import com.app.entity.Standard;
import com.app.entity.Subject;
import com.app.service.StandardService;
import com.app.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private StandardService standardService;

    // List all subjects
    @GetMapping
    public String listSubjects(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "admin/subject_list";
    }

    // Show create form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("subject", new Subject());
        model.addAttribute("standards", standardService.getAllStandards());
        return "admin/subject_form";
    }

    // Save new subject
    @PostMapping("/save")
    public String saveSubject(@ModelAttribute Subject subject, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("standards", standardService.getAllStandards());
            return "admin/subject_form";
        }

        if (subject.getStandard() != null && subject.getStandard().getId() != null) {
            Optional<Standard> std = standardService.getStandardById(subject.getStandard().getId());
            std.ifPresent(subject::setStandard);
        } else {
            subject.setStandard(null);
        }

        subjectService.saveSubject(subject);
        return "redirect:/admin/subjects";
    }

    // Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Subject> optSubject = subjectService.getSubjectById(id);
        if (!optSubject.isPresent()) {
            return "redirect:/admin/subjects";
        }
        model.addAttribute("subject", optSubject.get());
        model.addAttribute("standards", standardService.getAllStandards());
        return "admin/edit_subject";
    }

    // Update subject
    @PostMapping("/update/{id}")
    public String updateSubject(@PathVariable Long id, @ModelAttribute Subject subject, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("standards", standardService.getAllStandards());
            return "admin/edit_subject";
        }

        Optional<Standard> std = standardService.getStandardById(
                subject.getStandard() != null ? subject.getStandard().getId() : null);

        std.ifPresentOrElse(subject::setStandard, () -> subject.setStandard(null));
        subject.setId(id);
        subjectService.updateSubject(subject);
        return "redirect:/admin/subjects";
    }

    // Delete subject
    @GetMapping("/delete/{id}")
    public String deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return "redirect:/admin/subjects";
    }
    
}
