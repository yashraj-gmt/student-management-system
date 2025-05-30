package com.app.service.impl;

import com.app.entity.AcademicYear;
import com.app.repository.AcademicYearRepository;
import com.app.service.AcademicYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AcademicYearServiceImpl implements AcademicYearService {

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Override
    public List<AcademicYear> getAllAcademicYears() {
        return academicYearRepository.findAll();
    }

    @Override
    public Optional<AcademicYear> getAcademicYearById(long id) {
        return academicYearRepository.findById(id);
    }

    @Override
    public AcademicYear saveAcademicYear(AcademicYear academicYear) {
        return academicYearRepository.save(academicYear);
    }

    @Override
    public void deleteAcademicYear(long id) {
            academicYearRepository.deleteById(id);
    }
}
