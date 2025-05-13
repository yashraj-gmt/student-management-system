package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long>{

	@Query("Select s from Student s where " +
		       "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		       "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		       "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
		List<Student> findByKeyword(@Param("keyword") String keyword);
	
//	List<Student> findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String firstName, String email);

}
