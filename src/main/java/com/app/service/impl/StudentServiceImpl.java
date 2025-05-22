package com.app.service.impl;

import com.app.entity.Student;
import com.app.repository.StudentRepository;
import com.app.service.FileUploadService;
import com.app.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

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
    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public void deleteStudentById(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public List<Student> searchStudents(String keyword) {
        return studentRepository.findByKeyword(keyword);
    }

    @Override
    public Student registerStudent(Student student, List<String> hobbies, MultipartFile photo, MultipartFile aadhaarFile, MultipartFile panFile) throws IOException {
        student.setHobbies(String.join(",", hobbies));

        Student savedStudent = studentRepository.save(student); // Save first to get ID

        // Save photo
        String uploadDir = "student-photos/" + savedStudent.getId();
        if (!photo.isEmpty()) {
            String photoFileName = FileUploadService.saveFile(uploadDir, photo.getOriginalFilename(), photo);
            savedStudent.setPhoto(photoFileName);
        } else {
            savedStudent.setPhoto("user.png");
        }

        // Save Aadhaar
        if (!aadhaarFile.isEmpty()) {
            String uploadDirAd = "student-documents/" + savedStudent.getId() + "/aadhaar";
            String aadhaarFileName = FileUploadService.saveFile(uploadDirAd, aadhaarFile.getOriginalFilename(), aadhaarFile);
            savedStudent.setAadhaarFileName(aadhaarFileName);
        } else {
            savedStudent.setAadhaarFileName(null);
        }

        // Save PAN
        if (!panFile.isEmpty()) {
            String uploadDirPan = "student-documents/" + savedStudent.getId() + "/pan";
            String panFileName = FileUploadService.saveFile(uploadDirPan, panFile.getOriginalFilename(), panFile);
            savedStudent.setPanFileName(panFileName);
        } else {
            savedStudent.setPanFileName(null);
        }

        return studentRepository.save(savedStudent);
    }

    @Override
    public Student updateStudentWithFiles(Long id, Student updatedData, List<String> hobbies,
                                          MultipartFile photo, MultipartFile aadhaarFile, MultipartFile panFile) throws IOException {

        Student existingStudent = getStudentById(id);
        if (existingStudent == null) {
            return null;
        }


        existingStudent.setFirstName(updatedData.getFirstName());
        existingStudent.setLastName(updatedData.getLastName());
        existingStudent.setFatherName(updatedData.getFatherName());
        existingStudent.setMobileNumber(updatedData.getMobileNumber());
        existingStudent.setEmail(updatedData.getEmail());
        existingStudent.setGender(updatedData.getGender());
        existingStudent.setHobbies(String.join(",", hobbies));
        existingStudent.setDateOfBirth(updatedData.getDateOfBirth());
        existingStudent.setDescription(updatedData.getDescription());
        existingStudent.setCity(updatedData.getCity());

        // photo file
        String uploadDir = "student-photos/" + existingStudent.getId();
        if (photo != null && !photo.isEmpty()) {
            String photoFileName = FileUploadService.saveFile(uploadDir, photo.getOriginalFilename(), photo);
            existingStudent.setPhoto(photoFileName);
        } else {
            existingStudent.setPhoto(existingStudent.getPhoto());
        }

        // Aadhaar file
        if (aadhaarFile != null && !aadhaarFile.isEmpty()) {
            String uploadDirAd = "student-documents/" + existingStudent.getId() + "/aadhaar";
            String aadhaarFileName = FileUploadService.saveFile(uploadDirAd, aadhaarFile.getOriginalFilename(), aadhaarFile);
            existingStudent.setAadhaarFileName(aadhaarFileName);
        } else {
            existingStudent.setAadhaarFileName(existingStudent.getAadhaarFileName()); // retain existing Aadhaar
        }

        // PAN file
        if (panFile != null && !panFile.isEmpty()) {
            String uploadDirPan = "student-documents/" + existingStudent.getId() + "/pan";
            String panFileName = FileUploadService.saveFile(uploadDirPan, panFile.getOriginalFilename(), panFile);
            existingStudent.setPanFileName(panFileName);
        } else {
            existingStudent.setPanFileName(existingStudent.getPanFileName()); // retain existing PAN
        }

        return studentRepository.save(existingStudent);
    }

    public void validateStudent(Student student, MultipartFile photo,
                                MultipartFile aadhaarFile, MultipartFile panFile,
                                    BindingResult result) {

        if (student.getDateOfBirth() == null) {
            result.rejectValue("dateOfBirth", "error.student", "Please enter a valid date of birth.");
        }

        final long MAX_FILE_SIZE = 1 * 1024 * 1024;

        if (photo != null && photo.getSize() > MAX_FILE_SIZE) {
            result.rejectValue("photo", "error.student", "Photo file size exceeds 1MB limit.");
        }

        if (aadhaarFile != null && aadhaarFile.getSize() > MAX_FILE_SIZE) {
            result.rejectValue("aadhaarFileName", "error.student", "Aadhaar file size exceeds 1MB limit.");
        }

        if (panFile != null && panFile.getSize() > MAX_FILE_SIZE) {
            result.rejectValue("panFileName", "error.student", "PAN file size exceeds 1MB limit.");
        }
    }

    @Override
    public Page<Student> getPaginatedStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return studentRepository.findAll(pageable);
    }


}
