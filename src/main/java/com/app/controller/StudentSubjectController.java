package com.app.controller;

import com.app.entity.Student;
import com.app.entity.StudentSubject;
import com.app.entity.Subject;
import com.app.repository.StudentRepository;
import com.app.repository.StudentSubjectRepository;
import com.app.repository.SubjectRepository;
import com.app.service.StudentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/assign-subjects")
public class StudentSubjectController {

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final StudentService studentService;

    public StudentSubjectController(StudentRepository studentRepository, SubjectRepository subjectRepository,
                                    StudentSubjectRepository studentSubjectRepository, StudentService studentService) {
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.studentSubjectRepository = studentSubjectRepository;
        this.studentService = studentService;
    }

    @GetMapping
    public String showAssignForm(Model model) {
        List<Student> students = studentRepository.findAll();
        List<Subject> subjects = subjectRepository.findAll();

        // Build map of studentId -> List of subjectIds
        Map<Long, List<Long>> studentSubjectsMap = new HashMap<>();

        for (Student student : students) {
            List<Long> assignedSubjectIds = studentSubjectRepository.findByStudent(student)
                    .stream()
                    .map(ss -> ss.getSubject().getId())
                    .toList();
            studentSubjectsMap.put(student.getId(), assignedSubjectIds);
        }

        model.addAttribute("students", students);
        model.addAttribute("subjects", subjects);
        model.addAttribute("studentSubjectsMap", studentSubjectsMap);

        return "assign_subjects";
    }


    @PostMapping
    @Transactional
    public String assignSubjects(@RequestParam Long studentId,
                                 @RequestParam List<Long> subjectIds,
                                 RedirectAttributes redirectAttributes) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            // Delete existing assignments
            studentSubjectRepository.deleteByStudent(student);

            // Assign new subjects
            for (Long subjectId : subjectIds) {
                Subject subject = subjectRepository.findById(subjectId).orElse(null);
                if (subject != null) {
                    StudentSubject ss = new StudentSubject();
                    ss.setStudent(student);
                    ss.setSubject(subject);
                    studentSubjectRepository.save(ss);
                }
            }

            // Add flash message
            redirectAttributes.addFlashAttribute("successMessage", "Subjects assigned successfully!");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Student not found or invalid data!");
        }

        return "redirect:/assign-subjects";
    }

    /*   @PostMapping
    @Transactional
    public String assignSubjects(@RequestParam Long studentId, @RequestParam List<Long> subjectIds) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            // delete existing assignments
            studentSubjectRepository.deleteByStudent(student);

            // assign new subjects
            for (Long subjectId : subjectIds) {
                Subject subject = subjectRepository.findById(subjectId).orElse(null);
                if (subject != null) {
                    StudentSubject ss = new StudentSubject();
                    ss.setStudent(student);
                    ss.setSubject(subject);
                    studentSubjectRepository.save(ss);
                }
            }
        }
        return "redirect:/assign-subjects";
    }
*/
    @GetMapping("/student-subjects")
    public String showStudentSubjectList(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        System.out.println("Returning student-subjects view with students: " + students);
        return "student_subject_list";
    }

}
