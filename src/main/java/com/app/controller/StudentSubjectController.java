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

import java.util.List;

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

    /// / form to assign subjects to a student
    @GetMapping
    public String showAssignForm(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("subjects", subjectRepository.findAll());
        return "assign_subjects";
    }

//	@GetMapping("/test")
//	public String test() {
//		return "Hello World";
//	}

    @PostMapping
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

    // Handle subject assignment
//    @PostMapping
//    public String assignSubjects(@RequestParam Long studentId, @RequestParam List<Long> subjectIds) {
//        Student student = studentRepository.findById(studentId).orElse(null);
//        if (student != null) {
//            // Delete previous subject assignments for the student
//            studentSubjectRepository.deleteByStudent(student);
//
//            // Add new subject assignments for the student
//            for (Long subjectId : subjectIds) {
//                Subject subject = subjectRepository.findById(subjectId).orElse(null);
//                if (subject != null) {
//                    StudentSubject ss = new StudentSubject();
//                    ss.setStudent(student);
//                    ss.setSubject(subject);
//                    studentSubjectRepository.save(ss);
//                }
//            }
//        }
//        return "redirect:/assign-subjects";
//    }
//
//    // View subjects assigned to each student
//    @GetMapping("/student-subjects")
//    public String showStudentSubjectList(Model model) {
//        List<Student> students = studentService.getAllStudents();
//        model.addAttribute("students", students);
//        return "student_subject_list";
//    }


    /*
     * @Controller
     *
     * @RequestMapping("/assign-subjects") public class StudentSubjectController {
     *
     * private final StudentRepository studentRepository; private final
     * SubjectRepository subjectRepository; private final StudentSubjectRepository
     * studentSubjectRepository; private final StudentService studentService;
     *
     * public StudentSubjectController(StudentRepository studentRepository,
     * SubjectRepository subjectRepository, StudentSubjectRepository
     * studentSubjectRepository, StudentService studentService) {
     * this.studentRepository = studentRepository; this.subjectRepository =
     * subjectRepository; this.studentSubjectRepository = studentSubjectRepository;
     * this.studentService = studentService; }
     *
     * // Form to assign subjects to a student
     *
     * @GetMapping public String showAssignForm(Model model) {
     * model.addAttribute("students", studentRepository.findAll());
     * model.addAttribute("subjects", subjectRepository.findAll()); return
     * "assign_subjects"; }
     *
     * // Handle subject assignment
     *
     * @PostMapping public String assignSubjects(@RequestParam Long
     * studentId, @RequestParam List<Long> subjectIds) { Student student =
     * studentRepository.findById(studentId).orElse(null); if (student != null) {
     * for (Long subjectId : subjectIds) { Subject subject =
     * subjectRepository.findById(subjectId).orElse(null); if (subject != null) {
     * StudentSubject ss = new StudentSubject(); ss.setStudent(student);
     * ss.setSubject(subject); studentSubjectRepository.save(ss); } } } return
     * "redirect:/assign-subjects"; }
     */
    // View subjects assigned to each student

    @GetMapping("/student-subjects")
    public String showStudentSubjectList(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        System.out.println("Returning student-subjects view with students: " + students);
        return "student_subject_list";
    }

}
