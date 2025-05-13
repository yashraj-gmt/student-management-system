package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entity.Student;
import com.app.entity.StudentSubject;

import jakarta.transaction.Transactional;

@Transactional
public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {
    @Modifying
    @Query("DELETE FROM StudentSubject ss WHERE ss.student = :student")
    void deleteByStudent(@Param("student") Student student);
}
