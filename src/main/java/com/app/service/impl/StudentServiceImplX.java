/*
package com.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.entity.Student;
import com.app.repository.StudentRepository;
import com.app.service.StudentService;

@Service
public class StudentServiceImplX implements StudentService{

	private StudentRepository studentRepository;
	
	public StudentServiceImplX(StudentRepository studentRepository) {
		super();
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
		return studentRepository.findById(id).get();
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

	
//	public List<Student> searchByKeyword(String keyword) {
//	    if (keyword == null || keyword.isEmpty()) {
//	        return studentRepository.findAll();
//	    }
//	    return studentRepository.findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
//	}


}
*/
