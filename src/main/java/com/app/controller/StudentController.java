package com.app.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.app.entity.City;
import com.app.entity.Student;
import com.app.service.LocationService;
import com.app.service.StudentService;

@Controller
public class StudentController {

    private final StudentService studentService;
    private final LocationService locationService;

    @Autowired
    public StudentController(StudentService studentService, LocationService locationService) {
        this.studentService = studentService;
        this.locationService = locationService;
    }

    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }

    @GetMapping("/students/new")
    public String createStudentForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("genders", List.of("Male", "Female", "Other"));
        model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling", "Other"));
        model.addAttribute("states", locationService.getAllStates());
        return "create_student";
    }

    @PostMapping("/students")
    public String saveStudent(@ModelAttribute("student") Student student, BindingResult bindingResult,
                              @RequestParam("image") MultipartFile multipartFile,
                              @RequestParam("hobbies") List<String> hobbies,
                              @RequestParam("aadhaar") MultipartFile aadhaarFile,
                              @RequestParam("pan") MultipartFile panFile,
                              Model model) throws IOException {

        if (bindingResult.hasErrors()) {

            model.addAttribute("genders", List.of("Male", "Female", "Other"));
            model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling", "Other"));
            model.addAttribute("states", locationService.getAllStates());
            return "create_student";
        }

        // Check file sizes (max 1MB)
        if (multipartFile.getSize() > 1 * 1024 * 1024)
            bindingResult.rejectValue("photo", "error.student", "Photo file size exceeds 1MB limit.");

        if (aadhaarFile.getSize() > 1 * 1024 * 1024)
            bindingResult.rejectValue("aadhaarFileName", "error.student", "Aadhaar file size exceeds 1MB limit.");

        if (panFile.getSize() > 1 * 1024 * 1024)
            bindingResult.rejectValue("panFileName", "error.student", "PAN file size exceeds 1MB limit.");

        if (student.getDateOfBirth() == null)
            bindingResult.rejectValue("dateOfBirth", "error.student", "Please enter a valid date of birth.");

        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", List.of("Male", "Female", "Other"));
            model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling", "Other"));
            model.addAttribute("states", locationService.getAllStates());
            return "create_student";
        }

        studentService.registerStudent(student, hobbies, multipartFile, aadhaarFile, panFile);
        return "redirect:/students";
    }

    @GetMapping("/students/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        model.addAttribute("genders", List.of("Male", "Female", "Other"));
        model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling", "Other"));
        model.addAttribute("states", locationService.getAllStates());
        model.addAttribute("selectedHobbies", student.getHobbies() != null ? student.getHobbies().split(",") : new String[0]);

        if (student.getCity() != null && student.getCity().getState() != null) {
            model.addAttribute("cities", locationService.getCitiesByStateId(student.getCity().getState().getId()));
        }

        return "edit_student";
    }

    @PostMapping("/students/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute("student") Student student,
                                @RequestParam("hobbies") List<String> hobbies,
                                @RequestParam(value = "image", required = false) MultipartFile multipartFile,
                                @RequestParam(value = "aadhaar", required = false) MultipartFile aadhaarFile,
                                @RequestParam(value = "pan", required = false) MultipartFile panFile,
                                Model model) throws IOException {

        studentService.updateStudentWithFiles(id, student, hobbies, multipartFile, aadhaarFile, panFile);
        return "redirect:/students";
    }

    @DeleteMapping("/students/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/students/{id}/show")
    public String showStudent(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        return "show";
    }

    // AJAX: Load cities by state
    @GetMapping("/cities")
    @ResponseBody
    public List<City> getCitiesByState(@RequestParam Long stateId) {
        return locationService.getCitiesByStateId(stateId);
    }

    @GetMapping("/students/search")
    public String searchStudents(@RequestParam("keyword") String keyword, Model model) {
        List<Student> students = studentService.searchStudents(keyword);
        model.addAttribute("students", students);
        return "students";
    }
}

