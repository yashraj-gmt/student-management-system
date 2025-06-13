package com.app.controller;

import com.app.entity.City;
import com.app.entity.Student;
import com.app.repository.StudentRepository;
import com.app.repository.UserRepository;
import com.app.service.AcademicYearService;
import com.app.service.LocationService;
import com.app.service.StandardService;
import com.app.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository studentRepo;
    private final UserRepository userRepo;
    private final AcademicYearService academicYearService;
    private final LocationService locationService;
    private final StudentService studentService;
    private final StandardService standardService;


    private static final List<String> GENDERS = List.of("Male", "Female", "Other");
    private static final List<String> HOBBIES = List.of("Reading", "Sports", "Music", "Traveling", "Other");

    public StudentController(StudentRepository studentRepo,
                             UserRepository userRepo,
                             AcademicYearService academicYearService,
                             LocationService locationService,
                             StudentService studentService,
                             StandardService standardService) {
        this.studentRepo = studentRepo;
        this.userRepo = userRepo;
        this.academicYearService = academicYearService;
        this.locationService = locationService;
        this.studentService = studentService;
        this.standardService = standardService;
    }

    private void populateFormModel(Model model) {
        model.addAttribute("genders", GENDERS);
        model.addAttribute("hobbiesList", HOBBIES);
        model.addAttribute("states", locationService.getAllStates());
        model.addAttribute("academicYears", academicYearService.getAllAcademicYears());
    }

    @GetMapping("/home")
    public String studentHome(Authentication auth, Model model) {
        String email = auth.getName();
        Optional<Student> optionalStudent = studentRepo.findByEmail(email);

        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            if (!student.getProfileCompleted()) {
                return "redirect:/students/complete-profile";
            }
            model.addAttribute("student", student);  // <-- Add this line
        }
        return "student/profile";
    }


    @GetMapping("/complete-profile")
    public String completeProfileForm(Model model, Authentication auth) {
        String email = auth.getName();
        Student student = studentRepo.findByEmail(email).orElseThrow();
        model.addAttribute("student", student);
        model.addAttribute("readonlyFields", List.of("firstName", "lastName", "email", "enrollmentNumber", "standard", "academicYear"));
        populateFormModel(model);
        model.addAttribute("standards", standardService.getAllStandards());
        return "student/complete_profile";
    }

    @GetMapping("/stu/city")
    @ResponseBody
    public List<City> getCitiesByState(@RequestParam Long stateId) {
        return locationService.getCitiesByStateId(stateId);
    }

    @PostMapping("/complete-profile")
    public String handleProfileCompletion(@Valid @ModelAttribute("student") Student updatedStudent,
                                          Authentication auth,
                                          @RequestParam("hobbies") List<String> hobbies,
                                          @RequestParam("photoFile") MultipartFile photoFile,
                                          @RequestParam("aadhaarFile") MultipartFile aadhaarFile,
                                          @RequestParam("panFile") MultipartFile panFile,
                                          BindingResult bindingResult,
                                          Model model) throws IOException {

        String email = auth.getName();
        Student existingStudent = studentRepo.findByEmail(email).orElseThrow();

        studentService.validateStudent(updatedStudent, photoFile, aadhaarFile, panFile, bindingResult);

        if (bindingResult.hasErrors()) {
            populateFormModel(model);
            model.addAttribute("readonlyFields", List.of("firstName", "lastName", "email", "enrollmentNumber", "standard", "academicYear"));
            model.addAttribute("standards", standardService.getAllStandards());
            return "student/complete_profile";
        }



        // ✅ Update and save complete profile
        studentService.completeProfile(existingStudent, updatedStudent, hobbies, photoFile, aadhaarFile, panFile);

        // ✅ Redirect to /students/profile (GET) which already loads the student again
        return "redirect:/students/profile";
    }

    @GetMapping("/profile")
    public String studentProfilePage(Model model, Principal principal) {
        String email = principal.getName();
        Student student = studentRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));
        model.addAttribute("student", student);
        return "student/profile";
    }


}
