package ru.rpovetkin.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.rpovetkin.controller.model.PostDto;
import ru.rpovetkin.repository.PostRepository;
import ru.rpovetkin.repository.entity.Post;
import ru.rpovetkin.repository.entity.Tag;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
public class PostService {
    private static final Logger log = Logger.getLogger(PostService.class.getName());

    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;

    public PostService(PostRepository postRepository,
                       FileStorageService fileStorageService) {
        this.postRepository = postRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<PostDto> getAllPosts() {
        List<Post> allPost = postRepository.findAll();
        return allPost.stream().map(PostDto::new).toList();
    }

    public PostDto getPost(Long id) {
        Post post = postRepository.findById(id).get();
        return new PostDto(post);
    }

    public PostDto createPost(String title, String text, MultipartFile image, List<Tag> tags) throws IOException {
        // Сохраняем изображение
        String imagePath = fileStorageService.storeFile(image);

        // Создаем и сохраняем пост
        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        post.setImagePath(imagePath);
        post.setTags(tags);

        Post savedPost = postRepository.save(post);
        return new PostDto(savedPost);
    }
}
