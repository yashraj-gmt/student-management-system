package com.app.repository;

import com.app.entity.AcademicYear;
import com.app.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

	@Query("SELECT COUNT(s) FROM Student s WHERE s.enrollmentNumber LIKE CONCAT('STD-', :year, '%')")
	Long countByEnrollmentYear(@Param("year") int year);

	@Query("SELECT s FROM Student s JOIN FETCH s.city JOIN FETCH s.state JOIN FETCH s.standard JOIN FETCH s.academicYear")
	List<Student> findAllWithRelations();


	// Simple list search by keyword (firstName, lastName, email)
	@Query("SELECT s FROM Student s WHERE " +
			"LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) ")
	List<Student> searchStudentsByKeyword(@Param("keyword") String keyword);

	// Paging search by firstName, lastName, or email (case-insensitive)
	Page<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
			String firstName, String lastName, Pageable pageable);

	// Paging search by keyword using custom query (single param)
	@Query("SELECT s FROM Student s WHERE " +
			"LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) ")
	Page<Student> searchByKeywordWithPaging(@Param("keyword") String keyword, Pageable pageable);

	Page<Student> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);


}
