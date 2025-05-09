package com.app.service;

import com.app.entity.Subject;
import java.util.List;

public interface SubjectService {

    List<Subject> getAllSubjects();

    Subject getSubjectById(Long id);

    Subject saveSubject(Subject subject);

    void deleteSubjectById(Long id);
}

