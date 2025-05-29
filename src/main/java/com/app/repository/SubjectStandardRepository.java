package com.app.repository;

import com.app.entity.SubjectStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectStandardRepository extends JpaRepository<SubjectStandard,Long> {
}
