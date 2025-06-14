package com.app.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "students")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String enrollmentNumber;

    private String firstName;
    private String lastName;
    private String email;
    private String fatherName;
    private String gender;
    private String hobbies;
    private String mobileNumber;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private State state;

    // Add these fields:
    private String token;

    @Column(name = "generated_password")
    private String generatedPassword;

//    @Transient
    private String photo;

    private String aadhaarFileName;

    private String panFileName;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth; // Date of Birth

    private String otp;
    private Long otpExpiry;

    @OneToOne
    private User userAccount; // Link to User credentials

    @ManyToOne(fetch = FetchType.LAZY)
    private Standard standard; // For pre-registration with standard


    // Student.java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id")
    private AcademicYear academicYear;

    private Boolean profileCompleted = false;

    public String getPhotosPath() {
        if (id == null || photo == null) {
            return "image/user.png";
        }
        return "student-photos/" + this.id + "/" + this.photo;
    }

    @Transient
    public String getAadhaarPath() {
        if (id == null || aadhaarFileName == null) {
            return null;
        }
        return "student-documents/" + this.id + "/aadhaar/" + this.aadhaarFileName;
    }

    @Transient
    public String getPanPath() {
        if (id == null || panFileName == null) {
            return null;
        }
        return "student-documents/" + this.id + "/pan/" + this.panFileName;
    }

    @ManyToMany
    @JoinTable(
            name = "studentsubject",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private List<Subject> subjects;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentSubject> studentSubjects;

    private String createdBy;  // admin or user

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentHistory> paymentHistories = new ArrayList<>();

}
