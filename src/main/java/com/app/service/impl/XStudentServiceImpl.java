//package com.app.service.impl;
//
//import com.app.entity.Student;
//import com.app.repository.StudentRepository;
////import com.app.service.FileUploadService;
//import com.app.service.FileUploadService;
//import com.app.service.StudentService;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//
//@Service
//public class StudentServiceImpl implements StudentService {
//
//    private final StudentRepository studentRepository;
//
//    public StudentServiceImpl(StudentRepository studentRepository) {
//        this.studentRepository = studentRepository;
//    }
//
//    @Override
//    public List<Student> getAllStudents() {
//        return studentRepository.findAll();
//    }
//
//    @Override
//    public Student saveStudent(Student student) {
//        return studentRepository.save(student);
//    }
//
//    @Override
//    public Student registerStudent(Student student, List<String> hobbies, MultipartFile photo,
//                                   MultipartFile aadhaarFile, MultipartFile panFile) throws IOException {
//
//        student.setHobbies(String.join(",", hobbies));
//
//        // Initially save to get the ID for file paths
//        Student savedStudent = studentRepository.save(student);
//
//        // Save Photo
//        if (photo != null && !photo.isEmpty()) {
//            String photoFileName = FileUploadService.saveFile("student-photos/" + savedStudent.getId(), photo.getOriginalFilename(), photo);
//            savedStudent.setPhoto(photoFileName);
//        } else {
//            savedStudent.setPhoto("user.png");
//        }
//
//        // Save Aadhaar File
//        if (aadhaarFile != null && !aadhaarFile.isEmpty()) {
//            String aadhaarFileName = FileUploadService.saveFile("student-documents/" + savedStudent.getId() + "/aadhaar", aadhaarFile.getOriginalFilename(), aadhaarFile);
//            savedStudent.setAadhaarFileName(aadhaarFileName);
//        }
//
//        // Save PAN File
//        if (panFile != null && !panFile.isEmpty()) {
//            String panFileName = FileUploadService.saveFile("student-documents/" + savedStudent.getId() + "/pan", panFile.getOriginalFilename(), panFile);
//            savedStudent.setPanFileName(panFileName);
//        }
//
//        // Set default profile completion status
//        savedStudent.setProfileCompleted(false);
//
//        return studentRepository.save(savedStudent);
//    }
//
//    @Override
//    public Student updateStudentWithFiles(Long id, Student updatedData, List<String> hobbies,
//                                          MultipartFile photo, MultipartFile aadhaarFile, MultipartFile panFile) throws IOException {
//
//        Student existingStudent = getStudentById(id);
//        if (existingStudent == null) return null;
//
//        // Basic fields
//        existingStudent.setFirstName(updatedData.getFirstName());
//        existingStudent.setLastName(updatedData.getLastName());
//        existingStudent.setFatherName(updatedData.getFatherName());
//        existingStudent.setGender(updatedData.getGender());
//        existingStudent.setMobileNumber(updatedData.getMobileNumber());
//        existingStudent.setDateOfBirth(updatedData.getDateOfBirth());
//        existingStudent.setDescription(updatedData.getDescription());
//        existingStudent.setCity(updatedData.getCity());
//        existingStudent.setStandard(updatedData.getStandard());
//        existingStudent.setSubjects(updatedData.getSubjects()); // Many-to-Many
//        existingStudent.setHobbies(String.join(",", hobbies));
//
//        // Photo file
//        if (photo != null && !photo.isEmpty()) {
//            String photoFileName = FileUploadService.saveFile("student-photos/" + id, photo.getOriginalFilename(), photo);
//            existingStudent.setPhoto(photoFileName);
//        }
//
//        // Aadhaar file
//        if (aadhaarFile != null && !aadhaarFile.isEmpty()) {
//            String aadhaarFileName = FileUploadService.saveFile("student-documents/" + id + "/aadhaar", aadhaarFile.getOriginalFilename(), aadhaarFile);
//            existingStudent.setAadhaarFileName(aadhaarFileName);
//        }
//
//        // PAN file
//        if (panFile != null && !panFile.isEmpty()) {
//            String panFileName = FileUploadService.saveFile("student-documents/" + id + "/pan", panFile.getOriginalFilename(), panFile);
//            existingStudent.setPanFileName(panFileName);
//        }
//
//        return studentRepository.save(existingStudent);
//    }
//
//    @Override
//    public void validateStudent(Student student, MultipartFile photo, MultipartFile aadhaarFile,
//                                MultipartFile panFile, BindingResult result) {
//        final long MAX_FILE_SIZE = 1 * 1024 * 1024;
//
//        if (student.getDateOfBirth() == null) {
//            result.rejectValue("dateOfBirth", "error.student", "Please enter a valid date of birth.");
//        }
//
//        if (photo != null && photo.getSize() > MAX_FILE_SIZE) {
//            result.rejectValue("photo", "error.student", "Photo file size exceeds 1MB limit.");
//        }
//
//        if (aadhaarFile != null && aadhaarFile.getSize() > MAX_FILE_SIZE) {
//            result.rejectValue("aadhaarFileName", "error.student", "Aadhaar file size exceeds 1MB limit.");
//        }
//
//        if (panFile != null && panFile.getSize() > MAX_FILE_SIZE) {
//            result.rejectValue("panFileName", "error.student", "PAN file size exceeds 1MB limit.");
//        }
//    }
//
//    @Override
//    public Student getStudentById(Long id) {
//        return studentRepository.findById(id).orElse(null);
//    }
//
//    @Override
//    public Page<Student> getPaginatedStudents(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return studentRepository.findAll(pageable);
//    }
//
//    @Override
//    public Page<Student> searchStudents(String keyword, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        if (keyword == null || keyword.trim().isEmpty()) {
//            return studentRepository.findAll(pageable);
//        }
//        return studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword, pageable);
//    }
//
//    @Override
//    public Page<Student> search(String keyword, Pageable pageable) {
//        if (keyword == null || keyword.isEmpty()) {
//            return studentRepository.findAll(pageable);
//        } else {
//            return studentRepository.findByFirstNameContainingIgnoreCase(keyword, pageable);
//        }
//    }
//
//
//
//
//}
