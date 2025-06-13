package com.app.repository;

import com.app.entity.AcademicYear;
import com.app.entity.StandardWiseStudentCount;
import com.app.entity.Student;
import com.app.entity.StudentStandardCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT COUNT(s) FROM Student s WHERE s.enrollmentNumber LIKE CONCAT('STD-', :year, '%')")
    Long countByEnrollmentYear(@Param("year") int year);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.city c LEFT JOIN FETCH s.state st LEFT JOIN FETCH s.standard std")
    List<Student> findAllWithRelations();


    Optional<Student> findByUserAccountEmail(String email);

    Optional<Student> findByOtp(String otp);

    Optional<Student> findByEmail(String email);

    Page<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrGenderContainingIgnoreCaseOrHobbiesContainingIgnoreCaseOrState_NameContainingIgnoreCaseOrCity_NameContainingIgnoreCaseOrStandard_NameContainingIgnoreCaseOrCreatedByContainingIgnoreCase(String firstName, String lastName, String gender, String hobbies, String state, String city, String standard, String createdBy, Pageable pageable);

    Page<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrStandard_NameContainingIgnoreCase(
            String firstName,
            String lastName,
            String standardName,
            Pageable pageable
    );

    @Query(value = """
        SELECT st.name AS standardName, COUNT(s.id) AS studentCount
        FROM students s
        JOIN standard st ON s.standard_id = st.id
        GROUP BY st.name
    """, nativeQuery = true)
    List<StandardWiseStudentCount> getStandardWiseStudentCount();

}
