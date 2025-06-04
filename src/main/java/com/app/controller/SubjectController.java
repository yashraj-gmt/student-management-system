package com.app.controller;

import com.app.entity.Standard;
import com.app.entity.Subject;
import com.app.service.StandardService;
import com.app.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private StandardService standardService;

    @GetMapping
    public String listSubjects(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "admin/subject_list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("subject", new Subject());
        model.addAttribute("standards", standardService.getAllStandards());
        return "admin/subject_form";
    }

    @PostMapping("/save")
    public String saveSubject(@Valid @ModelAttribute("subject") Subject subject, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("standards", standardService.getAllStandards());
            return "admin/subject_form";
        }

        // Assign standard properly
        if (subject.getStandard() != null && subject.getStandard().getId() != null) {
            standardService.getStandardById(subject.getStandard().getId())
                    .ifPresent(subject::setStandard);
        } else {
            subject.setStandard(null);
        }

        subjectService.saveSubject(subject);
        return "redirect:/admin/subjects";
    }

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

    @PostMapping("/update/{id}")
    public String updateSubject(@PathVariable Long id,
                                @Valid @ModelAttribute("subject") Subject subject,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("standards", standardService.getAllStandards());
            return "admin/edit_subject";
        }

        if (subject.getStandard() != null && subject.getStandard().getId() != null) {
            standardService.getStandardById(subject.getStandard().getId())
                    .ifPresent(subject::setStandard);
        } else {
            subject.setStandard(null);
        }

        subject.setId(id);
        subjectService.updateSubject(subject);
        return "redirect:/admin/subjects";
    }

    @GetMapping("/delete/{id}")
    public String deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return "redirect:/admin/subjects";
    }
}
