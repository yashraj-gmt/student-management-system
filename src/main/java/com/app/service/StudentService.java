package com.app.service;

import java.io.IOException;
import java.util.List;

import com.app.entity.Student;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {
	List<Student> getAllStudents();
	
	Student saveStudent(Student student);
	
	Student getStudentById(Long id);
	
	Student updateStudent(Student student);
	
	void deleteStudentById(Long id);

	List<Student> searchStudents(String keyword);
	
	Student registerStudent(Student student,
							List<String> hobbies,
							MultipartFile photo, MultipartFile aadhaarFile, MultipartFile panFile) throws IOException;

	Student updateStudentWithFiles(Long id, Student updatedData, List<String> hobbies,
								   MultipartFile photo, MultipartFile aadhaarFile, MultipartFile panFile) throws IOException;


}
