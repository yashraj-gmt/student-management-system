package com.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_subjects")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
}
