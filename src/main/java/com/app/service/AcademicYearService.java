package com.app.service;

import com.app.entity.AcademicYear;

import java.util.List;
import java.util.Optional;

public interface AcademicYearService {

    List<AcademicYear> getAllAcademicYears();

    Optional<AcademicYear> getAcademicYearById(long id);

    AcademicYear saveAcademicYear(AcademicYear academicYear);

    void deleteAcademicYear(long id);
}
