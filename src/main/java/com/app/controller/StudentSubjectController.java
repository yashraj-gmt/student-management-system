package com.app.controller;

import com.app.entity.Student;
import com.app.entity.Subject;
import com.app.service.StudentService;
import com.app.service.StudentSubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/assign-subjects")
public class StudentSubjectController {

    private final StudentService studentService;
    private final StudentSubjectService studentSubjectService;

    public StudentSubjectController(StudentService studentService, StudentSubjectService studentSubjectService) {
        this.studentService = studentService;
        this.studentSubjectService = studentSubjectService;
    }

    // showing assigned subjects student wise
    @GetMapping
    public String showAssignForm(Model model) {
        List<Student> students = studentService.getAllStudents();
        List<Subject> subjects = studentSubjectService.getAllSubjects();

        Map<Long, List<Long>> studentSubjectsMap = studentSubjectService.getStudentSubjectsMap(students);

        model.addAttribute("students", students);
        model.addAttribute("subjects", subjects);
        model.addAttribute("studentSubjectsMap", studentSubjectsMap);

        return "assign_subjects";
    }

    // assigning subject to student
    @PostMapping
    public String assignSubjects(@RequestParam Long studentId,
                                 @RequestParam List<Long> subjectIds,
                                 RedirectAttributes redirectAttributes) {
        boolean success = studentSubjectService.assignSubjects(studentId, subjectIds);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Subjects assigned successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found or invalid data!");
        }

        return "redirect:/assign-subjects";
    }

    // getting list
    @GetMapping("/student-subjects")
    public String showStudentSubjectList(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "student_subject_list";
    }
}
