package ru.rpovetkin.service;

import org.springframework.stereotype.Service;
import ru.rpovetkin.controller.model.PostDto;
import ru.rpovetkin.repository.PostRepository;
import ru.rpovetkin.repository.entity.Post;

import java.util.List;
import java.util.logging.Logger;

@Service
public class PostService {
    private static final Logger log = Logger.getLogger(PostService.class.getName());

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostDto> getAllPosts() {
        List<Post> allPost = postRepository.findAll();
        return allPost.stream().map(PostDto::new).toList();
    }

    public PostDto getPost(Long id) {
        Post post = postRepository.findById(id).get();
        return new PostDto(post);
    }
}
