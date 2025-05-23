package com.app.service;

import java.io.IOException;
import java.util.List;

import com.app.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {
	List<Student> getAllStudents();

	Student saveStudent(Student student);

	Student getStudentById(Long id);

	Student updateStudent(Student student);

	void deleteStudentById(Long id);

	Student registerStudent(Student student,
							List<String> hobbies,
							MultipartFile photo, MultipartFile aadhaarFile, MultipartFile panFile) throws IOException;

	Student updateStudentWithFiles(Long id, Student updatedData, List<String> hobbies,
								   MultipartFile photo, MultipartFile aadhaarFile, MultipartFile panFile) throws IOException;

	void validateStudent(Student student, MultipartFile photo,
						 MultipartFile aadhaarFile, MultipartFile panFile,
						 BindingResult result);

	Page<Student> getPaginatedStudents(int page, int size);

	Page<Student> searchStudents(String keyword, int page, int size);

	Page<Student> search(String keyword, Pageable pageable);

}
