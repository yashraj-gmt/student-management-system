package com.app.service.impl;

import com.app.entity.Student;
import com.app.entity.StudentSubject;
import com.app.entity.Subject;
import com.app.repository.StudentRepository;
import com.app.repository.StudentSubjectRepository;
import com.app.repository.SubjectRepository;
import com.app.service.StudentSubjectService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentSubjectServiceImpl implements StudentSubjectService {

    @Autowired
    private StudentSubjectRepository studentSubjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    @Transactional
    public boolean assignSubjects(Long studentId, List<Long> subjectIds) {
        return studentRepository.findById(studentId).map(student -> {
            studentSubjectRepository.deleteByStudent(student);
            subjectIds.forEach(id -> subjectRepository.findById(id).ifPresent(subject -> {
                StudentSubject ss = new StudentSubject();
                ss.setStudent(student);
                ss.setSubject(subject);
                studentSubjectRepository.save(ss);
            }));
            return true;
        }).orElse(false);
    }

    public Map<Long, List<Long>> getStudentSubjectsMap(List<Student> students) {
        Map<Long, List<Long>> map = new HashMap<>();
        for (Student student : students) {
            List<Long> subjectIds = studentSubjectRepository.findByStudent(student)
                    .stream()
                    .map(ss -> ss.getSubject().getId())
                    .toList();
            map.put(student.getId(), subjectIds);
        }
        return map;
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

}
