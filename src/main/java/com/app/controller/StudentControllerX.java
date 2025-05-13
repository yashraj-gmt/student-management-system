/*
package com.app.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.app.entity.City;
import com.app.entity.Student;
import com.app.service.FileUploadService;
import com.app.service.LocationService;
import com.app.service.StudentService;
import com.app.service.impl.LocationServiceImpl;

@Controller
public class StudentControllerX {

	private final LocationServiceImpl locationServiceImpl;

	private final StudentService studentService;
	private final LocationService locationService;

	@Autowired
	public StudentControllerX(StudentService studentService, LocationService locationService,
							  LocationServiceImpl locationServiceImpl) {
		this.studentService = studentService;
		this.locationService = locationService;
		this.locationServiceImpl = locationServiceImpl;
	}

	@GetMapping("/students")
	public String listStudents(Model model) {
		model.addAttribute("students", studentService.getAllStudents());
		return "students";
	}

	@GetMapping("/students/new")
	public String createStudentForm(Model model) {
		model.addAttribute("student", new Student());
		model.addAttribute("genders", List.of("Male", "Female", "Other"));
		model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling","Other"));
		model.addAttribute("states", locationService.getAllStates());
		return "create_student";
	}

	@PostMapping("/students")
	public String saveStudent(@ModelAttribute("student") Student student, BindingResult bindingResult,
			@RequestParam("image") MultipartFile multipartFile, @RequestParam("hobbies") List<String> hobbies,
			@RequestParam("aadhaar") MultipartFile aadhaarFile, @RequestParam("pan") MultipartFile panFile,
			Model model) throws IOException {

		student.setHobbies(String.join(",", hobbies));

		if (bindingResult.hasErrors()) {
			model.addAttribute("genders", List.of("Male", "Female", "Other"));
			model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling"));
			model.addAttribute("states", locationService.getAllStates());
			return "create_student";
		}

		// 1 mb for photo, 1 mb for Aadhaar and PAN
		if (!multipartFile.isEmpty() && multipartFile.getSize() > 1 * 1024 * 1024) {   ///1048576 bytes
			bindingResult.rejectValue("photo", "error.student", "File size exceeds 1MB limit.");
			model.addAttribute("genders", List.of("Male", "Female", "Other"));
			model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling"));
			model.addAttribute("states", locationService.getAllStates());
			return "create_student";
		}

		if (!aadhaarFile.isEmpty() && aadhaarFile.getSize() > 1 * 1024 * 1024) {
			bindingResult.rejectValue("aadhaarFileName", "error.student", "Aadhaar file size exceeds 1MB limit.");
			model.addAttribute("genders", List.of("Male", "Female", "Other"));
			model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling"));
			model.addAttribute("states", locationService.getAllStates());
			return "create_student";
		}

		if (!panFile.isEmpty() && panFile.getSize() > 1 * 1024 * 1024) {
			bindingResult.rejectValue("panFileName", "error.student", "PAN file size exceeds 1MB limit.");
			model.addAttribute("genders", List.of("Male", "Female", "Other"));
			model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling"));
			model.addAttribute("states", locationService.getAllStates());
			return "create_student";
		}

		// first saving  to get id
		Student savedStudent = studentService.saveStudent(student);

		// upload directory
		String uploadDir = "student-photos/" + savedStudent.getId(); 

		// Save photo
		if (!multipartFile.isEmpty()) {
		    String photoFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		    
		    String savedFileName = FileUploadService.saveFile(uploadDir, photoFileName, multipartFile);
		    student.setPhoto(savedFileName);  

		    try {
		        FileUploadService.saveFile(uploadDir, savedFileName, multipartFile);
		    } catch (IOException e) {
		        e.printStackTrace();
		        model.addAttribute("fileUploadError", "Failed to upload image. Please try again.");
		        model.addAttribute("genders", List.of("Male", "Female", "Other"));
		        model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling"));
		        model.addAttribute("states", locationService.getAllStates());
		        return "create_student";
		    }
		    savedStudent.setPhoto(savedFileName);  
		    studentService.updateStudent(savedStudent);
		} else {
		    savedStudent.setPhoto("user.png");  // Default image if no file uploaded
		    studentService.updateStudent(savedStudent);
		}

		// Save Aadhaar
		if (!aadhaarFile.isEmpty()) {
			String aadhaarFileName = StringUtils.cleanPath(aadhaarFile.getOriginalFilename());
			student.setAadhaarFileName(aadhaarFileName);

			String uploadDirAd = "student-documents/" + savedStudent.getId() + "/aadhaar";
			try {
				FileUploadService.saveFile(uploadDirAd, aadhaarFileName, aadhaarFile);
			} catch (IOException e) {
				e.printStackTrace();
				model.addAttribute("fileUploadError", "Failed to upload Aadhaar. Please try again.");
				return "create_student";
			}
			savedStudent.setAadhaarFileName(aadhaarFileName);
			studentService.updateStudent(savedStudent);
		}

		// Save PAN
		if (!panFile.isEmpty()) {
			String panFileName = StringUtils.cleanPath(panFile.getOriginalFilename());
			student.setPanFileName(panFileName);

			String uploadDirPan = "student-documents/" + savedStudent.getId() + "/pan";
			try {
				FileUploadService.saveFile(uploadDirPan, panFileName, panFile);
			} catch (IOException e) {
				e.printStackTrace();
				model.addAttribute("fileUploadError", "Failed to upload PAN. Please try again.");
				return "create_student";
			}
			savedStudent.setPanFileName(panFileName);
			studentService.updateStudent(savedStudent);
		}

		return "redirect:/students";
	}

	@GetMapping("/students/edit/{id}")
	public String editStudentForm(@PathVariable Long id, Model model) {
	    Student student = studentService.getStudentById(id);
	    model.addAttribute("student", student);
	    model.addAttribute("genders", List.of("Male", "Female", "Other"));
	    model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling"));
	    model.addAttribute("states", locationService.getAllStates());

	    // Set the hobbies as a list 
	    model.addAttribute("selectedHobbies", student.getHobbies() != null ? student.getHobbies().split(",") : new String[0]);

	    // Load cities for the selected state of the student
	    if (student.getCity() != null && student.getCity().getState() != null) {
	        model.addAttribute("cities", locationService.getCitiesByStateId(student.getCity().getState().getId()));
	    }

	    return "edit_student";
	}

	@PostMapping("/students/{id}")
	public String updateStudent(@PathVariable Long id, @ModelAttribute("student") Student student,
	        @RequestParam("hobbies") List<String> hobbies, 
	        @RequestParam(value = "image", required = false) MultipartFile multipartFile,
	        @RequestParam(value = "aadhaar", required = false) MultipartFile aadhaarFile,
	        @RequestParam(value = "pan", required = false) MultipartFile panFile,
	        Model model) throws IOException {

	    Student existingStudent = studentService.getStudentById(id);
	    existingStudent.setFirstName(student.getFirstName());
	    existingStudent.setLastName(student.getLastName());
	    existingStudent.setFatherName(student.getFatherName());
	    existingStudent.setMobileNumber(student.getMobileNumber());
	    existingStudent.setEmail(student.getEmail());
	    existingStudent.setGender(student.getGender());
	    existingStudent.setHobbies(String.join(",", hobbies)); 
	    existingStudent.setDescription(student.getDescription());
	    existingStudent.setCity(student.getCity());

	    String uploadDir = "student-photos/" + existingStudent.getId();  

	    
	    if (!multipartFile.isEmpty()) {
	        String photoFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	        
	        String savedFileName = FileUploadService.saveFile(uploadDir, photoFileName, multipartFile);
	        existingStudent.setPhoto(savedFileName); 

	        // Save the photo to the directory
	        try {
	            FileUploadService.saveFile(uploadDir, savedFileName, multipartFile);
	        } catch (IOException e) {
	            e.printStackTrace();
	            model.addAttribute("fileUploadError", "Failed to upload photo. Please try again.");
	            return "edit_student";
	        }
	    }
		

	    if (!aadhaarFile.isEmpty()) {
	        String aadhaarFileName = StringUtils.cleanPath(aadhaarFile.getOriginalFilename());
	        existingStudent.setAadhaarFileName(aadhaarFileName);

	        String uploadDirAd = "student-documents/" + existingStudent.getId() + "/aadhaar";
	        try {
	            FileUploadService.saveFile(uploadDirAd, aadhaarFileName, aadhaarFile);
	        } catch (IOException e) {
	            e.printStackTrace();
	            model.addAttribute("fileUploadError", "Failed to upload Aadhaar. Please try again.");
	            return "edit_student";
	        }
	    }

	    if (!panFile.isEmpty()) {
	        String panFileName = StringUtils.cleanPath(panFile.getOriginalFilename());
	        existingStudent.setPanFileName(panFileName);

	        String uploadDirPan = "student-documents/" + existingStudent.getId() + "/pan";
	        try {
	            FileUploadService.saveFile(uploadDirPan, panFileName, panFile);
	        } catch (IOException e) {
	            e.printStackTrace();
	            model.addAttribute("fileUploadError", "Failed to upload PAN. Please try again.");
	            return "edit_student";
	        }
	    }

	   
	    studentService.updateStudent(existingStudent);
	    return "redirect:/students";
	}

	
	
	
	@DeleteMapping("/students/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
	    studentService.deleteStudentById(id);
	    return ResponseEntity.ok().build();
	}


	@GetMapping("/students/{id}/show")
	public String showStudent(@PathVariable Long id, Model model) {
		Student student = studentService.getStudentById(id);
		model.addAttribute("student", student);
		return "show"; 
	}

	// AJAX endpoint for dynamic city dropdown
	@GetMapping("/cities")
	@ResponseBody
	public List<City> getCitiesByState(@RequestParam Long stateId) {
		return locationService.getCitiesByStateId(stateId);
	}

	@GetMapping("/students/search")
	public String searchStudents(@RequestParam("keyword") String keyword, Model model) {
	    List<Student> students = studentService.searchStudents(keyword);
	    model.addAttribute("students", students);
	    return "students";
	}

}
*/
