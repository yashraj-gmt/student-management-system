package com.app.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.app.entity.StandardWiseStudentCount;
import com.app.entity.Student;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.naming.factory.SendMailFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {
    List<Student> getAllStudents();

    void completeProfile(Student existingStudent, Student formInput, List<String> hobbies, MultipartFile photoFile, MultipartFile aadhaarFile, MultipartFile panFile) throws IOException;

    Student saveStudent(Student student);

	void deleteStudent(Long id);

	Student updateStudent(Long id, Student updatedStudent, List<String> hobbies,
						  MultipartFile photo, MultipartFile aadhaarFile, MultipartFile panFile) throws IOException;

    Student registerStudent(Student student, List<String> hobbies, MultipartFile photo,
                            MultipartFile aadhaarFile, MultipartFile panFile) throws IOException;

    Student getStudentById(Long id);

    void validateStudent(Student student, MultipartFile photo, MultipartFile aadhaarFile,
                         MultipartFile panFile, BindingResult result);

    List<Student> getAllStudentsWithRelations();

    Optional<Student> findByOtp(String otp);

    Optional<Student> findByEmail(String email);

    Page<Student> searchStudents(String keyword, Pageable pageable);

    Page<Student> getAllStudentsPaginated(Pageable pageable);

    void deleteStudentsByIds(List<Long> ids);

    List<StandardWiseStudentCount> fetchStandardWiseStudentCount();

    void exportStudentsToCSV(List<Student> students, HttpServletResponse response) throws IOException;

    void exportStudentsToPDF(List<Student> students, HttpServletResponse response) throws IOException;

    void exportStudentsToExcel(List<Student> students, HttpServletResponse response) throws IOException;
}
