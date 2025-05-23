package com.app.controller;

import java.io.IOException;
import java.util.List;

import com.app.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.app.entity.City;
import com.app.entity.Student;
import com.app.service.LocationService;
import com.app.service.StudentService;

/*
    controller for handling student related operations like
    creating, updating, listing, deleting, and searching
*/
@Controller
public class StudentController {

    private final StudentService studentService;
    private final LocationService locationService;
    private static final List<String> GENDERS = List.of("Male", "Female", "Other");
    private static final List<String> HOBBIES = List.of("Reading", "Sports", "Music", "Traveling", "Other");

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    public StudentController(StudentService studentService, LocationService locationService) {
        this.studentService = studentService;
        this.locationService = locationService;
    }

    private void populateFormModel(Model model) {
        model.addAttribute("genders", GENDERS);
        model.addAttribute("hobbiesList", HOBBIES);
        model.addAttribute("states", locationService.getAllStates());
    }

    @GetMapping("/students")
    public String listStudents(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               @RequestParam(required = false) String keyword,
                               Model model) {

        Page<Student> studentPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            studentPage = studentService.searchStudents(keyword, page, size);
            model.addAttribute("keyword", keyword);
        } else {
            studentPage = studentService.getPaginatedStudents(page, size);
        }

        model.addAttribute("studentPage", studentPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("students", studentPage.getContent());

        return "students"; // Thymeleaf template
    }



    // For AJAX-based search (partial refresh)
/*    @GetMapping("/students/search")
    public String searchStudentsAjax(
            @RequestParam(name = "search", required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        if (keyword == null) keyword = ""; // optional, to avoid null in service

        Page<Student> studentPage = studentService.searchStudents(keyword, page, size);
        model.addAttribute("studentPage", studentPage);
        model.addAttribute("currentPage", page);
        return "fragments/student-table :: tableBody";
    }*/

    @GetMapping("/students/search")
    public String searchStudents(
            @RequestParam("search") String keyword,
            @RequestParam("page") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentPage = studentService.search(keyword, pageable);
        model.addAttribute("studentPage", studentPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        return "fragments/student_table :: tableBody";  // return Thymeleaf fragment
    }


    // method for adding new student
    @GetMapping("/students/new")
    public String createStudentForm(Model model) {
        model.addAttribute("student", new Student());
        populateFormModel(model);
        return "create_student";
    }

    // Save students data
    @PostMapping("/students")
    public String saveStudent(@ModelAttribute("student") Student student, BindingResult bindingResult,
                              @RequestParam("image") MultipartFile photo,
                              @RequestParam("hobbies") List<String> hobbies,
                              @RequestParam("aadhaar") MultipartFile aadhaarFile,
                              @RequestParam("pan") MultipartFile panFile,
                              Model model) throws IOException {

        studentService.validateStudent(student, photo, aadhaarFile, panFile, bindingResult);

        if (bindingResult.hasErrors()) {
            populateFormModel(model);
            return "create_student";
        }

        studentService.registerStudent(student, hobbies, photo, aadhaarFile, panFile);
        return "redirect:/students";
    }

    // update student form method
    @GetMapping("/students/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        populateFormModel(model);
        model.addAttribute("selectedHobbies", student.getHobbies() != null ? student.getHobbies().split(",") : new String[0]);

        if (student.getCity() != null && student.getCity().getState() != null) {
            model.addAttribute("cities", locationService.getCitiesByStateId(student.getCity().getState().getId()));
        }

        return "edit_student";
    }

    @PostMapping("/students/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute("student") Student student,
                                @RequestParam("hobbies") List<String> hobbies,
                                BindingResult bindingResult,
                                @RequestParam(value = "image", required = false) MultipartFile photo,
                                @RequestParam(value = "aadhaar", required = false) MultipartFile aadhaarFile,
                                @RequestParam(value = "pan", required = false) MultipartFile panFile,
                                Model model) throws IOException {

        studentService.validateStudent(student, photo, aadhaarFile, panFile, bindingResult);

        if (bindingResult.hasErrors()) {
            populateFormModel(model);
            model.addAttribute("selectedHobbies", hobbies.toArray(new String[0]));

            if (student.getCity() != null && student.getCity().getState() != null) {
                model.addAttribute("cities", locationService.getCitiesByStateId(student.getCity().getState().getId()));
            }

            return "edit_student";
        }

        studentService.updateStudentWithFiles(id, student, hobbies, photo, aadhaarFile, panFile);
        return "redirect:/students";
    }

    // delete student data
    @DeleteMapping("/students/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        return ResponseEntity.ok().build();
    }

    // show student profile
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
    // searching student

/*    @GetMapping("/students/search")
    public String searchStudents(@RequestParam("keyword") String keyword, Model model) {
        List<Student> students = studentService.searchStudents(keyword);
        model.addAttribute("students", students);
        return "students";
    }*/

//    @GetMapping("/search-students")
//    @ResponseBody
//    public List<Student> searchStudents(@RequestParam("keyword") String keyword) {
////        Pageable limit = PageRequest.of(0, 10);
//        return studentRepository.searchStudentsByKeyword(keyword);
//    }

    @GetMapping("/students/all")
    @ResponseBody
    public List<Student> getAllStudents() {
        return studentService.getAllStudents(); // ensure this returns all student data including nested city/state
    }

    @GetMapping("/students/paged")
    public String getPagedStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        if (page < 0) page = 0;
        if (size <= 0) size = 5;

        Page<Student> studentPage = studentService.getPaginatedStudents(page, size);

        model.addAttribute("studentPage", studentPage); // the whole Page object for detailed info
        model.addAttribute("students", studentPage.getContent()); // the current page content list
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("hasPrev", studentPage.hasPrevious());
        model.addAttribute("hasNext", studentPage.hasNext());

        return "students";
    }

}

