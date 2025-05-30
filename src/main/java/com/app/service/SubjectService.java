package com.app.service;

import com.app.entity.Subject;
import java.util.List;
import java.util.Optional;

public interface SubjectService {

    List<Subject> getAllSubjects();

    Optional<Subject> getSubjectById(Long id);

    Subject saveSubject(Subject subject);

    void deleteSubjectById(Long id);
    List<Subject> getSubjectsByIds(List<Long> ids);

     Subject updateSubject(Subject subject);

    void deleteSubject(Long id);
}

