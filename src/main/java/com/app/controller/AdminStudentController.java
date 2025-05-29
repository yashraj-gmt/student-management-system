package com.app.controller;

import java.io.IOException;
import java.util.List;

import com.app.entity.Subject;
import com.app.repository.StudentRepository;
import com.app.entity.City;
import com.app.entity.Student;
import com.app.repository.SubjectRepository;
import com.app.service.LocationService;
import com.app.service.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/students")
public class AdminStudentController {

    @Autowired
    private SubjectRepository subjectRepository;

    private final StudentService studentService;
    private final LocationService locationService;
    private static final List<String> GENDERS = List.of("Male", "Female", "Other");
    private static final List<String> HOBBIES = List.of("Reading", "Sports", "Music", "Traveling", "Other");

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    public AdminStudentController(StudentService studentService, LocationService locationService) {
        this.studentService = studentService;
        this.locationService = locationService;
    }

    private void populateFormModel(Model model) {
        model.addAttribute("genders", GENDERS);
        model.addAttribute("hobbiesList", HOBBIES);
        model.addAttribute("states", locationService.getAllStates());
    }

    @GetMapping("/stuManage")
    public String studentManagement() {
        return "admin/students"; // This loads admin/students.html
    }

    @GetMapping
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
        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", studentPage.getTotalPages());

        return "admin/students";
    }



//    @GetMapping("/search")
//    public String searchStudents(@RequestParam("search") String keyword,
//                                 @RequestParam("page") int page,
//                                 @RequestParam(value = "size", defaultValue = "5") int size,
//                                 Model model) {
//
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Student> studentPage = studentService.search(keyword, pageable);
//
//        model.addAttribute("studentPage", studentPage);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("pageSize", size);
//        model.addAttribute("keyword", keyword);
//        model.addAttribute("totalPages", studentPage.getTotalPages());
//
//        return "admin/student_list :: tableBody";
//    }

    @GetMapping("/new")
    public String createStudentForm(Model model) {
        model.addAttribute("student", new Student());
        populateFormModel(model);
        return "admin/create_student";
    }

    @PostMapping
    public String saveStudent(@ModelAttribute("student") Student student, BindingResult bindingResult,
                              @RequestParam("image") MultipartFile photo,
                              @RequestParam("hobbies") List<String> hobbies,
                              @RequestParam("aadhaar") MultipartFile aadhaarFile,
                              @RequestParam("pan") MultipartFile panFile,
                              Model model) throws IOException {

        studentService.validateStudent(student, photo, aadhaarFile, panFile, bindingResult);

        if (bindingResult.hasErrors()) {
            populateFormModel(model);
            return "admin/create_student";
        }

        studentService.registerStudent(student, hobbies, photo, aadhaarFile, panFile);
        return "redirect:/admin/students";
    }


    @GetMapping("/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        populateFormModel(model);
        model.addAttribute("selectedHobbies", student.getHobbies() != null ? student.getHobbies().split(",") : new String[0]);
        if (student.getCity() != null && student.getCity().getState() != null) {
            model.addAttribute("cities", locationService.getCitiesByStateId(student.getCity().getState().getId()));
        }
        return "admin/edit_student";
    }

    @PostMapping("/{id}")
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
            return "admin/edit_student";
        }

        studentService.updateStudentWithFiles(id, student, hobbies, photo, aadhaarFile, panFile);
        return "redirect:/admin/students";
    }

//    @DeleteMapping("/{id}")
//    @ResponseBody
//    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
//        studentService.deleteStudentById(id);
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/{id}/show")
    public String showStudent(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        return "admin/show";
    }

    @GetMapping("/cities")
    @ResponseBody
    public List<City> getCitiesByState(@RequestParam Long stateId) {
        return locationService.getCitiesByStateId(stateId);
    }

    @GetMapping("/all")
    @ResponseBody
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }
//
//    @GetMapping("/paged")
//    public String getPagedStudents(@RequestParam(defaultValue = "0") int page,
//                                   @RequestParam(defaultValue = "5") int size,
//                                   Model model) {
//        if (page < 0) page = 0;
//        if (size <= 0) size = 5;
//
//        Page<Student> studentPage = studentService.getPaginatedStudents(page, size);
//
//        model.addAttribute("studentPage", studentPage);
//        model.addAttribute("students", studentPage.getContent());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", studentPage.getTotalPages());
//        model.addAttribute("pageSize", size);
//        model.addAttribute("hasPrev", studentPage.hasPrevious());
//        model.addAttribute("hasNext", studentPage.hasNext());
//
//        return "admin/student_list";
//    }

    @GetMapping("/subjects")
    @ResponseBody
    public List<Subject> getSubjectsByStandard(@RequestParam("standardId") Long standardId) {
        return subjectRepository.findByStandardId(standardId);
    }

}
