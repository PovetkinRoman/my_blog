package ru.rpovetkin.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    // Директория для сохранения (вне WAR-файла)
    private static final String UPLOAD_DIR = "uploads/images";

    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }
        try {
            // Получаем абсолютный путь к директории
            Path uploadPath = Paths.get(System.getProperty("catalina.base"), "webapps", UPLOAD_DIR);

            // Создаем директорию, если не существует
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Генерируем уникальное имя файла
            String filename = System.currentTimeMillis() + getFileExtension(file.getOriginalFilename());

            // Сохраняем файл
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed save file");
        }
    }

    public Resource getFile(String filename) {
        try {
            Path uploadPath = Paths.get(System.getProperty("catalina.base"), "webapps", UPLOAD_DIR);
            Path filePath = uploadPath.resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
}
