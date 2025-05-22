package com.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Absolute paths to folders
		String studentPhotoDir = Paths.get("student-photos").toAbsolutePath().toUri().toString();
		String documentDir = Paths.get("student-documents").toAbsolutePath().toUri().toString();

		// Map URLs to physical folders
		registry.addResourceHandler("/student-photos/**").addResourceLocations(studentPhotoDir);
		registry.addResourceHandler("/student-documents/**").addResourceLocations(documentDir);
	}
}
