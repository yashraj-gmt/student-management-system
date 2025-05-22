package com.app.repository;

import org.springframework.data.domain.Pageable;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entity.Student;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>{

	@Query("Select s from Student s where " +
		       "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		       "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		       "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
		List<Student> findByKeyword(@Param("keyword") String keyword);

	@Query("SELECT s FROM Student s WHERE s.firstName LIKE %:keyword%")
	List<Student> searchStudentsByKeyword(@Param("keyword") String keyword);


	List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String firstName, String lastName, String email);

	Page<Student> findAll(Pageable pageable);

}
