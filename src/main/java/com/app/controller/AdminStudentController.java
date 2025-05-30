package com.app.controller;

import com.app.entity.City;
import com.app.entity.Student;
import com.app.repository.StudentRepository;
import com.app.service.AcademicYearService;
import com.app.service.LocationService;
import com.app.service.StandardService;
import com.app.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/students")
public class AdminStudentController {

    private final StudentService studentService;
    private final StandardService standardService;
    private final AcademicYearService academicYearService;
    private final LocationService locationService;
    private final StudentRepository studentRepository;

    private static final List<String> GENDERS = List.of("Male", "Female", "Other");
    private static final List<String> HOBBIES = List.of("Reading", "Sports", "Music", "Traveling", "Other");

    public AdminStudentController(StudentService studentService,
                                  StandardService standardService,
                                  AcademicYearService academicYearService,
                                  LocationService locationService, StudentRepository studentRepository) {
        this.studentService = studentService;
        this.standardService = standardService;
        this.academicYearService = academicYearService;
        this.locationService = locationService;
        this.studentRepository = studentRepository;
    }

    private void populateFormModel(Model model) {
        model.addAttribute("genders", GENDERS);
        model.addAttribute("hobbiesList", HOBBIES);
        model.addAttribute("states", locationService.getAllStates());
        model.addAttribute("standards", standardService.getAllStandards());
        model.addAttribute("academicYears", academicYearService.getAllAcademicYears());
    }

    @GetMapping("/register")
    public String showStudentRegistrationForm(Model model) {
        model.addAttribute("student", new Student());
        populateFormModel(model);
        return "admin/admin_student_registration";
    }

    @GetMapping("/api/cities")
    @ResponseBody
    public List<City> getCitiesByState(@RequestParam Long stateId) {
        return locationService.getCitiesByStateId(stateId);
    }

    @PostMapping("/register")
    public String registerStudent(
            @Valid @ModelAttribute("student") Student student,
            @RequestParam("hobbies") List<String> hobbies,
            @RequestParam("photoFile") MultipartFile photoFile,
            @RequestParam("aadhaarFile") MultipartFile aadhaarFile,
            @RequestParam("panFile") MultipartFile panFile,
            BindingResult bindingResult,
            Model model) throws IOException {

        studentService.validateStudent(student, photoFile, aadhaarFile, panFile, bindingResult);

        if (bindingResult.hasErrors()) {
            populateFormModel(model);
            return "admin/admin_student_registration";
        }

        studentService.registerStudent(student, hobbies, photoFile, aadhaarFile, panFile);

        return "redirect:/admin/manageStudents";
    }

    @GetMapping("/manageStudents")
    public String manageStudents(Model model) {
        List<Student> students = studentRepository.findAllWithRelations();
        model.addAttribute("students", students);
        return "students";
    }


}
