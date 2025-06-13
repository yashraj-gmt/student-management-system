package com.app.service.impl;

import com.app.entity.StandardWiseStudentCount;
import com.app.entity.Student;
import com.app.entity.User;
import com.app.repository.StudentRepository;
import com.app.repository.UserRepository;
import com.app.service.EmailService;
import com.app.service.FileUploadService;
import com.app.service.StudentService;
import com.itextpdf.text.Font;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.PrintWriter;
import java.util.stream.Stream;


import java.io.IOException;
import java.io.PrintWriter;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {

    private static final long MAX_FILE_SIZE = 1 * 1024 * 1024;

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public StudentServiceImpl(StudentRepository studentRepository, UserRepository userRepository, EmailService emailService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public Student updateStudent(Long id, Student updatedStudent, List<String> hobbies, MultipartFile photo, MultipartFile aadhaarFile, MultipartFile panFile) throws IOException {

        Student existingStudent = studentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Student Id"));

        existingStudent.setFirstName(updatedStudent.getFirstName());
        existingStudent.setLastName(updatedStudent.getLastName());
        existingStudent.setDateOfBirth(updatedStudent.getDateOfBirth());
        existingStudent.setFatherName(updatedStudent.getFatherName());
        existingStudent.setGender(updatedStudent.getGender());
        existingStudent.setMobileNumber(updatedStudent.getMobileNumber());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setCity(updatedStudent.getCity());
        existingStudent.setState(updatedStudent.getState());
        existingStudent.setDescription(updatedStudent.getDescription());
        existingStudent.setAcademicYear(updatedStudent.getAcademicYear());
        existingStudent.setStandard(updatedStudent.getStandard());

        if (hobbies != null && !hobbies.isEmpty()) {
            existingStudent.setHobbies(String.join(",", hobbies));
        }

        // Upload new files if provided
        if (photo != null && !photo.isEmpty()) {
            String photoFileName = FileUploadService.saveFile("student-photos/" + id, photo.getOriginalFilename(), photo);
            existingStudent.setPhoto(photoFileName);
        }

        if (aadhaarFile != null && !aadhaarFile.isEmpty()) {
            String aadhaarFileName = FileUploadService.saveFile("student-documents/" + id + "/aadhaar", aadhaarFile.getOriginalFilename(), aadhaarFile);
            existingStudent.setAadhaarFileName(aadhaarFileName);
        }

        if (panFile != null && !panFile.isEmpty()) {
            String panFileName = FileUploadService.saveFile("student-documents/" + id + "/pan", panFile.getOriginalFilename(), panFile);
            existingStudent.setPanFileName(panFileName);
        }

        return studentRepository.save(existingStudent);
    }

//    @Override
//    public Student registerStudent(Student student, List<String> hobbies, MultipartFile photo,
//                                   MultipartFile aadhaarFile, MultipartFile panFile) throws IOException {
//
//        // Join hobbies into single comma-separated string
//        if (hobbies != null && !hobbies.isEmpty()) {
//            student.setHobbies(String.join(",", hobbies));
//        }
//
//        // Generate enrollment number
//        student.setEnrollmentNumber(generateEnrollmentNumber());
//
//        // Set initial profile status
//        student.setProfileCompleted(false);
//
//        // Save student initially (to generate ID)
//        Student savedStudent = studentRepository.save(student);
//
//        // Upload photo
//        if (photo != null && !photo.isEmpty()) {
//            String photoFileName = FileUploadService.saveFile("student-photos/" + savedStudent.getId(),
//                    photo.getOriginalFilename(), photo);
//            savedStudent.setPhoto(photoFileName);
//        } else {
//            savedStudent.setPhoto("user.png");
//        }
//
//        // Upload Aadhaar
//        if (aadhaarFile != null && !aadhaarFile.isEmpty()) {
//            String aadhaarFileName = FileUploadService.saveFile("student-documents/" + savedStudent.getId() + "/aadhaar",
//                    aadhaarFile.getOriginalFilename(), aadhaarFile);
//            savedStudent.setAadhaarFileName(aadhaarFileName);
//        }
//
//        // Upload PAN
//        if (panFile != null && !panFile.isEmpty()) {
//            String panFileName = FileUploadService.saveFile("student-documents/" + savedStudent.getId() + "/pan",
//                    panFile.getOriginalFilename(), panFile);
//            savedStudent.setPanFileName(panFileName);
//        }
//
//        // Save updated student
//        return studentRepository.save(savedStudent);
//    }

    @Override
    public Student registerStudent(Student student, List<String> hobbies, MultipartFile photo,
                                   MultipartFile aadhaarFile, MultipartFile panFile) throws IOException {

        // Join hobbies into single comma-separated string
        if (hobbies != null && !hobbies.isEmpty()) {
            student.setHobbies(String.join(",", hobbies));
        }

        // Generate enrollment number
        student.setEnrollmentNumber(generateEnrollmentNumber());

        // Set initial profile status
        student.setProfileCompleted(false);

        // Save student initially (to generate ID)
        Student savedStudent = studentRepository.save(student);

        // Upload photo
        if (photo != null && !photo.isEmpty()) {
            String photoFileName = FileUploadService.saveFile("student-photos/" + savedStudent.getId(),
                    photo.getOriginalFilename(), photo);
            savedStudent.setPhoto(photoFileName);
        } else {
            savedStudent.setPhoto("user.png");
        }

        // Upload Aadhaar
        if (aadhaarFile != null && !aadhaarFile.isEmpty()) {
            String aadhaarFileName = FileUploadService.saveFile("student-documents/" + savedStudent.getId() + "/aadhaar",
                    aadhaarFile.getOriginalFilename(), aadhaarFile);
            savedStudent.setAadhaarFileName(aadhaarFileName);
        }

        // Upload PAN
        if (panFile != null && !panFile.isEmpty()) {
            String panFileName = FileUploadService.saveFile("student-documents/" + savedStudent.getId() + "/pan",
                    panFile.getOriginalFilename(), panFile);
            savedStudent.setPanFileName(panFileName);
        }

        // Generate random password
        String randomPassword = generateRandomPassword(10);

        // Create User entity linked to Student
        User user = new User();
        user.setEmail(student.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(randomPassword));
        user.setPasswordUpdated(true); // Mark as password already set
        // Save User first to get user id (optional if cascade is set)
        user = userRepository.save(user);

        // Link user account to student
        savedStudent.setUserAccount(user);

        // Save updated student with user link and files
        savedStudent = studentRepository.save(savedStudent);

        // Send welcome email with generated password
        emailService.sendWelcomeEmailWithPassword(student.getEmail(), student.getFirstName(), randomPassword);

        return savedStudent;
    }

    @Override
    public void completeProfile(Student existingStudent, Student formInput, List<String> hobbies, MultipartFile photo, MultipartFile aadhaarFile, MultipartFile panFile) throws IOException {
        existingStudent.setFatherName(formInput.getFatherName());
        existingStudent.setGender(formInput.getGender());
        existingStudent.setMobileNumber(formInput.getMobileNumber());
        existingStudent.setCity(formInput.getCity());
        existingStudent.setState(formInput.getState());
        existingStudent.setDescription(formInput.getDescription());
        existingStudent.setHobbies(String.join(",", hobbies));
        existingStudent.setProfileCompleted(true);
        existingStudent.setCreatedBy("user");

        // Save student initially (to generate ID)
        Student savedStudent = studentRepository.save(existingStudent);

        // Upload photo
        if (photo != null && !photo.isEmpty()) {
            String photoFileName = FileUploadService.saveFile("student-photos/" + savedStudent.getId(),
                    photo.getOriginalFilename(), photo);
            savedStudent.setPhoto(photoFileName);
        } else {
            savedStudent.setPhoto("user.png");
        }

        // Upload Aadhaar
        if (aadhaarFile != null && !aadhaarFile.isEmpty()) {
            String aadhaarFileName = FileUploadService.saveFile("student-documents/" + savedStudent.getId() + "/aadhaar",
                    aadhaarFile.getOriginalFilename(), aadhaarFile);
            savedStudent.setAadhaarFileName(aadhaarFileName);
        }

        // Upload PAN
        if (panFile != null && !panFile.isEmpty()) {
            String panFileName = FileUploadService.saveFile("student-documents/" + savedStudent.getId() + "/pan",
                    panFile.getOriginalFilename(), panFile);
            savedStudent.setPanFileName(panFileName);
        }

        studentRepository.save(existingStudent);
    }

    public String generateEnrollmentNumber() {
        int currentYear = Year.now().getValue();

        Long count = studentRepository.countByEnrollmentYear(currentYear);

        long nextNumber = (count == null ? 0 : count) + 1;

        // Format with leading zeros, e.g., 00001
        String numberStr = String.format("%05d", nextNumber);

        return "STD-" + currentYear + "-" + numberStr;
    }


    @Override
    public void validateStudent(Student student, MultipartFile photo, MultipartFile aadhaarFile,
                                MultipartFile panFile, BindingResult result) {

        if (student.getDateOfBirth() == null) {
            result.reject("dateOfBirth", "Please enter a valid date of birth.");
        }

        if (photo != null && photo.getSize() > MAX_FILE_SIZE) {
            result.reject("photo", "Photo file size exceeds 1MB limit.");
        }

        if (aadhaarFile != null && aadhaarFile.getSize() > MAX_FILE_SIZE) {
            result.reject("aadhaarFileName", "Aadhaar file size exceeds 1MB limit.");
        }

        if (panFile != null && panFile.getSize() > MAX_FILE_SIZE) {
            result.reject("panFileName", "PAN file size exceeds 1MB limit.");
        }
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Student> getAllStudentsWithRelations() {
        return studentRepository.findAllWithRelations();
    }

    @Override
    public Optional<Student> findByOtp(String otp) {
        return studentRepository.findByOtp(otp);
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        return studentRepository.findByUserAccountEmail(email);
    }

    @Override
    public Page<Student> searchStudents(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return studentRepository.findAll(pageable);
        }
        String kw = keyword.trim();
        return studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrGenderContainingIgnoreCaseOrHobbiesContainingIgnoreCaseOrState_NameContainingIgnoreCaseOrCity_NameContainingIgnoreCaseOrStandard_NameContainingIgnoreCaseOrCreatedByContainingIgnoreCase(
                kw, kw, kw, kw, kw, kw, kw, kw, pageable);
    }

    @Override
    public Page<Student> getAllStudentsPaginated(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }


    public void deleteStudentsByIds(List<Long> ids) {
        studentRepository.deleteAllById(ids);
    }

    @Override
    public void exportStudentsToCSV(List<Student> students, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=students.csv");

        PrintWriter writer = response.getWriter();
        writer.println("Enrollment Number,First Name,Father's Name,Last Name,Email,Gender,Mobile,State,City,Standard");

        for (Student student : students) {
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    safe(student.getEnrollmentNumber()),
                    safe(student.getFirstName()),
                    safe(student.getFatherName()),
                    safe(student.getLastName()),
                    safe(student.getEmail()),
                    safe(student.getGender()),
                    safe(student.getMobileNumber()),
                    student.getState() != null ? safe(student.getState().getName()) : "",
                    student.getCity() != null ? safe(student.getCity().getName()) : "",
                    student.getStandard() != null ? safe(student.getStandard().getName()) : "");
        }
        writer.flush();
        writer.close();
    }

    @Override
    public void exportStudentsToPDF(List<Student> students, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=students.pdf");

        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Registered Students", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{3, 4, 4, 4, 5, 3, 4, 3, 3, 3});

            Stream.of("Enrollment No", "First Name", "Father's Name", "Last Name", "Email",
                            "Gender", "Mobile", "State", "City", "Standard")
                    .forEach(header -> {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table.addCell(cell);
                    });

            for (Student student : students) {
                table.addCell(safe(student.getEnrollmentNumber()));
                table.addCell(safe(student.getFirstName()));
                table.addCell(safe(student.getFatherName()));
                table.addCell(safe(student.getLastName()));
                table.addCell(safe(student.getEmail()));
                table.addCell(safe(student.getGender()));
                table.addCell(safe(student.getMobileNumber()));
                table.addCell(student.getState() != null ? safe(student.getState().getName()) : "");
                table.addCell(student.getCity() != null ? safe(student.getCity().getName()) : "");
                table.addCell(student.getStandard() != null ? safe(student.getStandard().getName()) : "");
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            throw new IOException("Error generating PDF: " + e.getMessage());
        }
    }

    @Override
    public void exportStudentsToExcel(List<Student> students, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=students.xlsx");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Students");

            Row header = sheet.createRow(0);
            String[] columns = {"Enrollment No", "First Name", "Father's Name", "Last Name", "Email", "Gender",
                    "Mobile", "State", "City", "Standard"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(safe(student.getEnrollmentNumber()));
                row.createCell(1).setCellValue(safe(student.getFirstName()));
                row.createCell(2).setCellValue(safe(student.getFatherName()));
                row.createCell(3).setCellValue(safe(student.getLastName()));
                row.createCell(4).setCellValue(safe(student.getEmail()));
                row.createCell(5).setCellValue(safe(student.getGender()));
                row.createCell(6).setCellValue(safe(student.getMobileNumber()));
                row.createCell(7).setCellValue(student.getState() != null ? safe(student.getState().getName()) : "");
                row.createCell(8).setCellValue(student.getCity() != null ? safe(student.getCity().getName()) : "");
                row.createCell(9).setCellValue(student.getStandard() != null ? safe(student.getStandard().getName()) : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&!";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public List<StandardWiseStudentCount> fetchStandardWiseStudentCount() {
        return studentRepository.getStandardWiseStudentCount();
    }


    private List<Student> searchStudentsForExport(String keyword) {
        Pageable pageable = Pageable.unpaged(); // Fetch all for export

        if (keyword == null || keyword.trim().isEmpty()) {
            return studentRepository.findAll();
        }
        String kw = keyword.trim();
        Page<Student> page = studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrGenderContainingIgnoreCaseOrHobbiesContainingIgnoreCaseOrState_NameContainingIgnoreCaseOrCity_NameContainingIgnoreCaseOrStandard_NameContainingIgnoreCaseOrCreatedByContainingIgnoreCase(
                kw, kw, kw, kw, kw, kw, kw, kw, pageable);
        return page.getContent();
    }


}
