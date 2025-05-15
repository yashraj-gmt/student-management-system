package com.app.entity;

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

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "father_name")
	private String fatherName;

	@Column(name = "mobile_number")
	private String mobileNumber;

	@Column(name = "email")
	private String email;

	@Column(name = "gender")
	private String gender;

	@Column(name = "hobbies")
	private String hobbies;
	
	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "city_id")
	private City city;
	
	@Column(name = "photo")
	private String photo;
	
	@Transient
	private String photosPath;
	
	@Column(name = "aadhaarFileName")
    private String aadhaarFileName;
	
	@Column(name = "panFileName")
    private String panFileName;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateOfBirth; // Date of Birth
	
	public String getPhotosPath() {
		if(id == null || photo == null) {
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
	    name = "student_subject",
	    joinColumns = @JoinColumn(name = "student_id"),
	    inverseJoinColumns = @JoinColumn(name = "subject_id")
	)
	private List<Subject> subjects;

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StudentSubject> studentSubjects;	
}
