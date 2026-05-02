package com.assignment3.project.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.images.base-dir:images}")
    private String baseDir;

    @Value("${app.docs.base-dir:docs}")
    private String docsBaseDir;

    @Value("${app.avatars.base-dir:avatars}")
    private String avatarsBaseDir;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif");
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    public String saveProjectImage(String projectFolderName, MultipartFile file) throws IOException {
        log.info("FileStorageService.saveProjectImage folder={}, originalFile={}", projectFolderName, file != null ? file.getOriginalFilename() : null);
        
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Image file size exceeds maximum allowed size of 10MB");
        }
        
        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image";
        String lower = original.toLowerCase();
        String contentType = file.getContentType();
        
        boolean hasValidExtension = ALLOWED_IMAGE_EXTENSIONS.stream().anyMatch(lower::endsWith);
        boolean hasValidContentType = contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
        
        if (!hasValidExtension && !hasValidContentType) {
            throw new IllegalArgumentException("Only image files (JPG, PNG, WEBP, GIF) are allowed");
        }
        
        String safeProject = toSafeName(projectFolderName);
        String safeFile = toSafeName(original);
        if (!safeFile.contains(".")) {
            if (contentType != null) {
                if (contentType.toLowerCase().contains("jpeg") || contentType.toLowerCase().contains("jpg")) {
                    safeFile = safeFile + ".jpg";
                } else if (contentType.toLowerCase().contains("png")) {
                    safeFile = safeFile + ".png";
                } else if (contentType.toLowerCase().contains("webp")) {
                    safeFile = safeFile + ".webp";
                } else if (contentType.toLowerCase().contains("gif")) {
                    safeFile = safeFile + ".gif";
                } else {
                    safeFile = safeFile + ".jpg";
                }
            } else {
                safeFile = safeFile + ".jpg";
            }
        }
        Path dir = Paths.get(baseDir, safeProject);
        Files.createDirectories(dir);
        Path target = dir.resolve(safeFile);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return Paths.get(baseDir, safeProject, safeFile).toString().replace('\\','/');
    }

    public String saveDocument(MultipartFile file) throws IOException {
        log.info("FileStorageService.saveDocument originalFile={}", file != null ? file.getOriginalFilename() : null);
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Document file is required");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Document file size exceeds maximum allowed size of 10MB");
        }
        
        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf";
        String lower = original.toLowerCase();
        String contentType = file.getContentType();
        if (!(lower.endsWith(".pdf") || (contentType != null && contentType.equalsIgnoreCase("application/pdf")))) {
            throw new IllegalArgumentException("Only PDF files are allowed for documents");
        }
        String safeFileName = toSafeName(original);
        if (!safeFileName.toLowerCase().endsWith(".pdf")) {
            safeFileName = safeFileName + ".pdf";
        }
        Path dir = Paths.get(docsBaseDir);
        Files.createDirectories(dir);
        Path target = dir.resolve(safeFileName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return Paths.get(docsBaseDir, safeFileName).toString().replace('\\','/');
    }

    public String saveAvatar(MultipartFile file) throws IOException {
        log.info("FileStorageService.saveAvatar originalFile={}", file != null ? file.getOriginalFilename() : null);
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Avatar file is required");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Avatar file size exceeds maximum allowed size of 10MB");
        }
        
        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "avatar.png";
        String lower = original.toLowerCase();
        String contentType = file.getContentType();
        
        boolean hasValidExtension = ALLOWED_IMAGE_EXTENSIONS.stream().anyMatch(lower::endsWith);
        boolean hasValidContentType = contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
        
        if (!hasValidExtension && !hasValidContentType) {
            throw new IllegalArgumentException("Only image files (JPG, PNG, WEBP, GIF) are allowed for avatars");
        }
        
        String safeFileName = toSafeName(original);
        if (!safeFileName.contains(".")) {
            safeFileName = safeFileName + ".png";
        }
        Path dir = Paths.get(avatarsBaseDir);
        Files.createDirectories(dir);
        Path target = dir.resolve(safeFileName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return Paths.get(avatarsBaseDir, safeFileName).toString().replace('\\','/');
    }

    private String toSafeName(String input) {
        if (input == null) return "file";
        String normalized = input;
        return normalized.replaceAll("[^a-zA-Z0-9._-]+", "-").replaceAll("-+", "-").replaceAll("^-|-$", "");
    }
}
