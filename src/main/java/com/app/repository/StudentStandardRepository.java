package com.app.repository;

import com.app.entity.StudentStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentStandardRepository extends JpaRepository<StudentStandard,Long> {
}
