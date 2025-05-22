package ru.rpovetkin.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rpovetkin.controller.model.PostDto;
import ru.rpovetkin.service.FileStorageService;
import ru.rpovetkin.service.PostService;

import java.util.logging.Logger;

@RestController
@RequestMapping("/images")
public class ImageController {
    private static final Logger log = Logger.getLogger(ImageController.class.getName());

    private final PostService postService;
    private final FileStorageService fileStorageService;

    public ImageController(PostService postService, FileStorageService fileStorageService) {
        this.postService = postService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> getImage(@PathVariable(name = "id") Long id) {
        try {
            PostDto post = postService.getPost(id);
            log.info("getImage: post_id= " + id + "post image path=" + post.imagePath());
            Resource file = fileStorageService.getFile(post.imagePath());
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(file);
        } catch (Exception e) {
            log.finest("getImage: " + e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
