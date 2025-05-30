package com.app.service.impl;

import com.app.entity.Student;
import com.app.repository.StudentRepository;
import com.app.service.FileUploadService;
import com.app.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {

    private static final long MAX_FILE_SIZE = 1 * 1024 * 1024;

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
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

        // Save updated student
        return studentRepository.save(savedStudent);
    }

    private String generateEnrollmentNumber() {
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
}
