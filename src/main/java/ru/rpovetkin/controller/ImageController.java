package ru.rpovetkin.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/images")
public class ImageController {
    private static final Logger log = Logger.getLogger(ImageController.class.getName());

    private final ResourceLoader resourceLoader;

    public ImageController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping(value = "/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> getImage(@PathVariable(name = "id") String id) {
        try {
            // Формируем путь к файлу
            log.info("getImage: getting image with id: " + id);
            Resource resource = resourceLoader.getResource("classpath:images/" + id + ".jpg");

            if (resource.exists() || resource.isReadable()) {
                log.fine("getImage: resource.exists()");
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                log.fine("getImage: ResponseEntity.notFound()");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.finest("getImage: " + e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
