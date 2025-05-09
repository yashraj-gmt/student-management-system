package com.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String dirName = "student-photos";

		Path studentPhotosDir = Paths.get(dirName);

		String studentPhotosPath = studentPhotosDir.toFile().getAbsolutePath();

		registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + studentPhotosPath + "/");

		registry.addResourceHandler("/student-photos/**").addResourceLocations("file:student-photos/");

		registry.addResourceHandler("/student-documents/**").addResourceLocations("file:student-documents/");
	}

}