package ru.rpovetkin.service;

import org.springframework.stereotype.Service;
import ru.rpovetkin.model.Post;
import ru.rpovetkin.repository.PostRepository;

import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Optional<Post> getPost(Long id) {
        return postRepository.findById(id);
    }
}
