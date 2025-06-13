package com.app.controller;

import com.app.entity.City;
import com.app.entity.Student;
import com.app.service.AcademicYearService;
import com.app.service.LocationService;
import com.app.service.StandardService;
import com.app.service.StudentService;
import com.itextpdf.text.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import org.springframework.util.StringUtils;
import java.util.List;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Stream;

@Controller
@RequestMapping("/admin/students")
public class AdminStudentController {

    private final StudentService studentService;
    private final StandardService standardService;
    private final AcademicYearService academicYearService;
    private final LocationService locationService;

    private static final List<String> GENDERS = List.of("Male", "Female", "Other");
    private static final List<String> HOBBIES = List.of("Reading", "Sports", "Music", "Traveling", "Other");

    public AdminStudentController(StudentService studentService,
                                  StandardService standardService,
                                  AcademicYearService academicYearService,
                                  LocationService locationService) {
        this.studentService = studentService;
        this.standardService = standardService;
        this.academicYearService = academicYearService;
        this.locationService = locationService;
    }

    private void populateFormModel(Model model) {
        model.addAttribute("genders", GENDERS);
        model.addAttribute("hobbiesList", HOBBIES);
        model.addAttribute("states", locationService.getAllStates());
        model.addAttribute("standards", standardService.getAllStandards());
        model.addAttribute("academicYears", academicYearService.getAllAcademicYears());
    }


    @GetMapping("")
    public String studentManagement() {
        return "admin/students";
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
        student.setCreatedBy("admin");
        studentService.registerStudent(student, hobbies, photoFile, aadhaarFile, panFile);
        model.addAttribute("successMsg", "Student registered successfully!");

        return "admin/admin_student_registration";
    }

    @GetMapping("/viewStudents")
    public String manageStudents(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "size", defaultValue = "5") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").descending());

        Page<Student> studentPage = studentService.searchStudents(keyword, pageable);

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalItems", studentPage.getTotalElements());
        
        return "admin/view_students";
    }

//    @GetMapping("/viewStudents")
//    public String manageStudents(Model model) {
//        List<Student> students = studentService.getAllStudentsWithRelations();
//        model.addAttribute("students", students);
//        return "admin/view_students";
//  }
//

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        populateFormModel(model);
        return "admin/edit_student";
    }

    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable Long id,
                                @Valid @ModelAttribute("student") Student student,
                                @RequestParam("hobbies") List<String> hobbies,
                                @RequestParam("photoFile") MultipartFile photo,
                                @RequestParam("aadhaarFile") MultipartFile aadhaar,
                                @RequestParam("panFile") MultipartFile pan,
                                BindingResult result,
                                Model model) throws IOException {

        studentService.validateStudent(student, photo, aadhaar, pan, result);

        if (result.hasErrors()) {
            populateFormModel(model);
            return "admin/edit_student";
        }

        studentService.updateStudent(id, student, hobbies, photo, aadhaar, pan);
        return "redirect:/admin/students/viewStudents";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/admin/students/viewStudents";
    }


    @GetMapping("/export/csv")
    public void exportToCSV(
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpServletResponse response) throws IOException {

        Pageable pageable = Pageable.unpaged(); // export all matching students
        Page<Student> studentPage = studentService.searchStudents(keyword, pageable);
        List<Student> students = studentPage.getContent();

        studentService.exportStudentsToCSV(students, response);
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpServletResponse response) throws IOException {

        Pageable pageable = Pageable.unpaged(); // export all matching students
        Page<Student> studentPage = studentService.searchStudents(keyword, pageable);
        List<Student> students = studentPage.getContent();

        studentService.exportStudentsToPDF(students, response);
    }

    @GetMapping("/export/excel")
    public void exportToExcel(
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpServletResponse response) throws IOException {

        Pageable pageable = Pageable.unpaged(); // export all matching students
        Page<Student> studentPage = studentService.searchStudents(keyword, pageable);
        List<Student> students = studentPage.getContent();

        studentService.exportStudentsToExcel(students, response);
    }


    ///if want to download only particular page data
   /* @GetMapping("/export/csv")
    public void exportToCSV(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            HttpServletResponse response) throws IOException {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // Fetch filtered data for export
        Page<Student> studentPage = studentService.searchStudents(keyword, pageable);
        List<Student> students = studentPage.getContent();

        // Call existing export logic, but now with filtered list
        studentService.exportStudentsToCSV(students, response);
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            HttpServletResponse response) throws IOException {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Student> studentPage = studentService.searchStudents(keyword, pageable);
        List<Student> students = studentPage.getContent();

        studentService.exportStudentsToPDF(students, response);
    }

    @GetMapping("/export/excel")
    public void exportToExcel(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            HttpServletResponse response) throws IOException {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Student> studentPage = studentService.searchStudents(keyword, pageable);
        List<Student> students = studentPage.getContent();

        studentService.exportStudentsToExcel(students, response);
    }

*/

    @PostMapping("/delete-multiple")
    public String deleteMultipleStudents(@RequestParam("studentIds") List<Long> studentIds) {
        studentService.deleteStudentsByIds(studentIds);
        return "redirect:/admin/students/viewStudents";
    }


    @GetMapping("/view/{id}")
    public String viewStudentProfile(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        return "/admin/view_student_profile"; // Name of your Thymeleaf template
    }

    @GetMapping("/check-email")
    @ResponseBody
    public ResponseEntity<Boolean> checkEmailUnique(@RequestParam("email") String email) {
        boolean emailExists = studentService.findByEmail(email).isPresent();
        return ResponseEntity.ok(!emailExists);
    }



}
