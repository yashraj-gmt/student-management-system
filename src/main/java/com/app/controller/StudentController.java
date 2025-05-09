package com.app.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
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
public class StudentController {

	private final LocationServiceImpl locationServiceImpl;

	private final StudentService studentService;
	private final LocationService locationService;

	@Autowired
	public StudentController(StudentService studentService, LocationService locationService,
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
		model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling"));
		model.addAttribute("states", locationService.getAllStates());
		return "create_student";
	}

	@PostMapping("/students")
	public String saveStudent(@ModelAttribute("student") Student student, BindingResult bindingResult,
			@RequestParam("image") MultipartFile multipartFile, @RequestParam("hobbies") List<String> hobbies,
			@RequestParam("aadhaar") MultipartFile aadhaarFile, @RequestParam("pan") MultipartFile panFile,
			Model model) {

		student.setHobbies(String.join(",", hobbies));

		if (bindingResult.hasErrors()) {
			model.addAttribute("genders", List.of("Male", "Female", "Other"));
			model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling"));
			model.addAttribute("states", locationService.getAllStates());
			return "create_student";
		}

		// Server-side file size validation (1 MB for photo, 1 MB for Aadhaar and PAN)
		if (!multipartFile.isEmpty() && multipartFile.getSize() > 1 * 1024 * 1024) {
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

		// Save the student record first (so you get an ID)
		Student savedStudent = studentService.saveStudent(student);

		// Save photo
		if (!multipartFile.isEmpty()) {
			String photoFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			student.setPhoto(photoFileName);

			String uploadDir = "student-photos/" + savedStudent.getId();
			try {
				FileUploadService.saveFile(uploadDir, photoFileName, multipartFile);
			} catch (IOException e) {
				e.printStackTrace();
				model.addAttribute("fileUploadError", "Failed to upload image. Please try again.");
				model.addAttribute("genders", List.of("Male", "Female", "Other"));
				model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music", "Traveling"));
				model.addAttribute("states", locationService.getAllStates());
				return "create_student";
			}
			savedStudent.setPhoto(photoFileName);
			studentService.updateStudent(savedStudent);
		} else {
			savedStudent.setPhoto("user.png");
			studentService.updateStudent(savedStudent);
		}

		// Save Aadhaar
		if (!aadhaarFile.isEmpty()) {
			String aadhaarFileName = StringUtils.cleanPath(aadhaarFile.getOriginalFilename());
			student.setAadhaarFileName(aadhaarFileName);

			String uploadDir = "student-documents/" + savedStudent.getId() + "/aadhaar";
			try {
				FileUploadService.saveFile(uploadDir, aadhaarFileName, aadhaarFile);
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

			String uploadDir = "student-documents/" + savedStudent.getId() + "/pan";
			try {
				FileUploadService.saveFile(uploadDir, panFileName, panFile);
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

	    // Set the hobbies as a list (similar to the creation form)
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
	        Model model) {

	    Student existingStudent = studentService.getStudentById(id);
	    existingStudent.setFirstName(student.getFirstName());
	    existingStudent.setLastName(student.getLastName());
	    existingStudent.setFatherName(student.getFatherName());
	    existingStudent.setMobileNumber(student.getMobileNumber());
	    existingStudent.setEmail(student.getEmail());
	    existingStudent.setGender(student.getGender());
	    existingStudent.setHobbies(String.join(",", hobbies));  // Update hobbies as a comma-separated string
	    existingStudent.setDescription(student.getDescription());
	    existingStudent.setCity(student.getCity());

	    // Handle file uploads for photo, Aadhaar, and PAN if provided
	    if (!multipartFile.isEmpty()) {
	        String photoFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	        existingStudent.setPhoto(photoFileName);

	        String uploadDir = "student-photos/" + existingStudent.getId();
	        try {
	            FileUploadService.saveFile(uploadDir, photoFileName, multipartFile);
	        } catch (IOException e) {
	            e.printStackTrace();
	            model.addAttribute("fileUploadError", "Failed to upload photo. Please try again.");
	            return "edit_student";
	        }
	    }

	    if (!aadhaarFile.isEmpty()) {
	        String aadhaarFileName = StringUtils.cleanPath(aadhaarFile.getOriginalFilename());
	        existingStudent.setAadhaarFileName(aadhaarFileName);

	        String uploadDir = "student-documents/" + existingStudent.getId() + "/aadhaar";
	        try {
	            FileUploadService.saveFile(uploadDir, aadhaarFileName, aadhaarFile);
	        } catch (IOException e) {
	            e.printStackTrace();
	            model.addAttribute("fileUploadError", "Failed to upload Aadhaar. Please try again.");
	            return "edit_student";
	        }
	    }

	    if (!panFile.isEmpty()) {
	        String panFileName = StringUtils.cleanPath(panFile.getOriginalFilename());
	        existingStudent.setPanFileName(panFileName);

	        String uploadDir = "student-documents/" + existingStudent.getId() + "/pan";
	        try {
	            FileUploadService.saveFile(uploadDir, panFileName, panFile);
	        } catch (IOException e) {
	            e.printStackTrace();
	            model.addAttribute("fileUploadError", "Failed to upload PAN. Please try again.");
	            return "edit_student";
	        }
	    }

	    // Update student details
	    studentService.updateStudent(existingStudent);
	    return "redirect:/students";
	}

	
	/*
	 * @PostMapping("/students") public String saveStudent(
	 * 
	 * @ModelAttribute("student") Student student, BindingResult bindingResult,
	 * 
	 * @RequestParam("image") MultipartFile multipartFile,
	 * 
	 * @RequestParam("hobbies") List<String> hobbies, Model model) {
	 * 
	 * student.setHobbies(String.join(",", hobbies));
	 * 
	 * if (bindingResult.hasErrors()) { model.addAttribute("genders",
	 * List.of("Male", "Female", "Other")); model.addAttribute("hobbiesList",
	 * List.of("Reading", "Sports", "Music", "Traveling"));
	 * model.addAttribute("states", locationService.getAllStates()); return
	 * "create_student"; }
	 * 
	 * // Server-side file size validation (5 MB = 5 * 1024 * 1024) if
	 * (!multipartFile.isEmpty() && multipartFile.getSize() > 5 * 1024 * 1024) {
	 * bindingResult.rejectValue("photo", "error.student",
	 * "File size exceeds 5MB limit."); model.addAttribute("genders",
	 * List.of("Male", "Female", "Other")); model.addAttribute("hobbiesList",
	 * List.of("Reading", "Sports", "Music", "Traveling"));
	 * model.addAttribute("states", locationService.getAllStates()); return
	 * "create_student"; }
	 * 
	 * // Save the student record first (so you get an ID) Student savedStudent =
	 * studentService.saveStudent(student);
	 * 
	 * if (!multipartFile.isEmpty()) { String fileName =
	 * StringUtils.cleanPath(multipartFile.getOriginalFilename());
	 * student.setPhoto(fileName);
	 * 
	 * String uploadDir = "student-photos/" + savedStudent.getId(); try {
	 * FileUploadService.saveFile(uploadDir, fileName, multipartFile); } catch
	 * (IOException e) { // Log the error and optionally add an error message to the
	 * model e.printStackTrace(); model.addAttribute("fileUploadError",
	 * "Failed to upload image. Please try again.");
	 * 
	 * // Refill form values model.addAttribute("genders", List.of("Male", "Female",
	 * "Other")); model.addAttribute("hobbiesList", List.of("Reading", "Sports",
	 * "Music", "Traveling")); model.addAttribute("states",
	 * locationService.getAllStates()); return "create_student"; }
	 * 
	 * 
	 * // Update student with photo filename savedStudent.setPhoto(fileName);
	 * studentService.updateStudent(savedStudent); } else { // Set default image if
	 * no upload savedStudent.setPhoto("user.png");
	 * studentService.updateStudent(savedStudent); }
	 * 
	 * return "redirect:/students"; }
	 */

	/*
	 * @GetMapping("/students/edit/{id}") public String
	 * editStudentForm(@PathVariable Long id, Model model) { Student student =
	 * studentService.getStudentById(id); model.addAttribute("student", student);
	 * model.addAttribute("genders", List.of("Male", "Female", "Other"));
	 * model.addAttribute("hobbiesList", List.of("Reading", "Sports", "Music",
	 * "Traveling")); model.addAttribute("states", locationService.getAllStates());
	 * 
	 * // Load cities for selected state if (student.getCity() != null &&
	 * student.getCity().getState() != null) { model.addAttribute("cities",
	 * locationService.getCitiesByStateId(student.getCity().getState().getId())); }
	 * 
	 * return "edit_student"; }
	 * 
	 * @PostMapping("/students/{id}") public String updateStudent(@PathVariable Long
	 * id, @ModelAttribute("student") Student student,
	 * 
	 * @RequestParam("hobbies") List<String> hobbies) { Student existingStudent =
	 * studentService.getStudentById(id);
	 * existingStudent.setFirstName(student.getFirstName());
	 * existingStudent.setLastName(student.getLastName());
	 * existingStudent.setFatherName(student.getFatherName());
	 * existingStudent.setMobileNumber(student.getMobileNumber());
	 * existingStudent.setEmail(student.getEmail());
	 * existingStudent.setGender(student.getGender());
	 * existingStudent.setHobbies(String.join(",", hobbies));
	 * existingStudent.setDescription(student.getDescription());
	 * existingStudent.setCity(student.getCity());
	 * 
	 * studentService.updateStudent(existingStudent); return "redirect:/students"; }
	 */
	@GetMapping("/students/{id}")
	public String deleteStudent(@PathVariable Long id) {
		studentService.deleteStudentById(id);
		return "redirect:/students";
	}

	@GetMapping("/students/{id}/show")
	public String showStudent(@PathVariable Long id, Model model) {
		Student student = studentService.getStudentById(id);
		model.addAttribute("student", student);
		return "show"; // this will map to show.html
	}

	// AJAX endpoint for dynamic city dropdown
	@GetMapping("/cities")
	@ResponseBody
	public List<City> getCitiesByState(@RequestParam Long stateId) {
		return locationService.getCitiesByStateId(stateId);
	}

}
