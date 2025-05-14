package ru.rpovetkin.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.rpovetkin.controller.model.PostDto;
import ru.rpovetkin.repository.CommentRepository;
import ru.rpovetkin.repository.PostRepository;
import ru.rpovetkin.repository.entity.Comment;
import ru.rpovetkin.repository.entity.Post;
import ru.rpovetkin.repository.entity.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class PostService {
    private static final Logger log = Logger.getLogger(PostService.class.getName());

    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository,
                       FileStorageService fileStorageService,
                       CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.fileStorageService = fileStorageService;
        this.commentRepository = commentRepository;
    }

    public PostDto getPost(Long id) {
        Post post = postRepository.findById(id).orElseGet(Post::new);
        return new PostDto(post);
    }

    public List<PostDto> getPostsWithFiltering(String search, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Post> posts = postRepository.findAll(pageable).getContent();

        List<Post> filteredPost = new ArrayList<>();
        if (!search.isEmpty()) {
            for (Post post : posts) {
                List<Tag> filteredTags = post.getTags().stream()
                        .filter(tag -> tag.getName().contains(search))
                        .toList();
                if (!filteredTags.isEmpty()) filteredPost.add(post);
            }
            return filteredPost.stream().map(PostDto::new).toList();
        }
        return posts.stream().map(PostDto::new).toList();
    }

    public PostDto createPost(String title, String text, MultipartFile image, String tags) {
        // Сохраняем изображение
        String imagePath = fileStorageService.storeFile(image);

        // Создаем и сохраняем пост
        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        post.setImagePath(imagePath);
        List<Tag> tagList = validateTags(tags, post);
        post.setTags(tagList);

        Post savedPost = postRepository.save(post);
        return new PostDto(savedPost);
    }

    public PostDto editPost(Long postId, String title, String text, MultipartFile image, String tags) throws IOException {
        Post post = postRepository.findById(postId).get();

        String imagePath = fileStorageService.storeFile(image);
        if (imagePath != null) post.setImagePath(imagePath);

        post.setTitle(title);
        post.setText(text);
        List<Tag> tagsList = validateTags(tags, post);
        post.setTags(tagsList);

        Post savedPost = postRepository.save(post);
        return new PostDto(savedPost);
    }

    public PostDto managerLikesCount(Long postId, Boolean isLike) {
        Post post = postRepository.findById(postId).get();
        post.setLikesCount(isLike ? post.getLikesCount() + 1 : post.getLikesCount() - 1);
        Post savedPost = postRepository.save(post);
        return new PostDto(savedPost);
    }

    public PostDto addCommentForPost(Long postId, String commentText) {
        if (commentText == null || commentText.isEmpty()) {
            throw new IllegalArgumentException("Comment text is null or empty");
        }
        Post post = postRepository.findById(postId).get();
        List<Comment> comments = post.getComments();
        Comment comment = new Comment();
        comment.setText(commentText);
        comment.setPost(post);
        comments.add(comment);
        postRepository.save(post);
        return new PostDto(post);
    }

    public PostDto editCommentForPost(Long postId, Long commentId, String commentText) {
        if (commentText == null || commentText.isEmpty()) {
            throw new IllegalArgumentException("Comment text is null or empty");
        }
        Comment commentEdit = commentRepository.findById(commentId).get();
        commentEdit.setText(commentText);
        commentRepository.save(commentEdit);
        Post post = postRepository.findById(postId).get();
        return new PostDto(post);
    }

    public PostDto deleteCommentForPost(Long postId, Long commentId) {
        Post post = postRepository.findById(postId).get();
        List<Comment> comments = post.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .toList();
        post.getComments().remove(comments.get(0));
        return new PostDto(postRepository.save(post));
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    private List<Tag> validateTags(String tags, Post post) {
        // Преобразуем строку тегов в список
        List<Tag> tagList = Arrays.stream(tags.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .distinct()
                .map(s -> {
                    Tag tag = new Tag();
                    tag.setName(s);
                    tag.setPost(post);
                    return tag;
                })
                .collect(Collectors.toList());
        return tagList;
    }
}
