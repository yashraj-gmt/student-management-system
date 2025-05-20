package com.app.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadService {

	public static String saveFile(String uploadDir, String originalFileName, MultipartFile multipartFile) throws IOException {
		Path uploadPath = Paths.get(uploadDir);

		// Create directories if they don't exist
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		// Get file extension
		String fileExtension = "";
		int dotIndex = originalFileName.lastIndexOf(".");
		if (dotIndex > 0) {
			fileExtension = originalFileName.substring(dotIndex); // includes dot
			originalFileName = originalFileName.substring(0, dotIndex);
		}

		// Create unique file name with timestamp
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String uniqueFileName = originalFileName + "_" + timestamp + fileExtension;

		// Save file to disk
		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadPath.resolve(uniqueFileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

			System.out.println("✅ File saved: " + filePath.toAbsolutePath());

			return uniqueFileName;
		} catch (IOException e) {
			System.err.println("❌ Failed to save file: " + originalFileName);
			throw new IOException("Could not save file " + originalFileName, e);
		}
	}
}