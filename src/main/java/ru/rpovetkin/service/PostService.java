package ru.rpovetkin.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.rpovetkin.controller.model.PostDto;
import ru.rpovetkin.dao.entity.Comment;
import ru.rpovetkin.dao.entity.Post;
import ru.rpovetkin.dao.entity.Tag;
import ru.rpovetkin.repository.CommentRepository;
import ru.rpovetkin.repository.PostRepository;
import ru.rpovetkin.repository.TagRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class PostService {
    private static final Logger log = Logger.getLogger(PostService.class.getName());

    private final FileStorageService fileStorageService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;

    public PostService(FileStorageService fileStorageService,
                       PostRepository postRepository,
                       CommentRepository commentRepository,
                       TagRepository tagRepository) {
        this.fileStorageService = fileStorageService;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.tagRepository = tagRepository;
    }

    public PostDto getPost(Long postId) {
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new IllegalArgumentException("post is not found");
        }
        return new PostDto(post);
    }

    public List<PostDto> getPostsWithFiltering(String search, Integer pageNumber, Integer pageSize) {
        List<Post> posts = postRepository.findAll(pageNumber, pageSize);
        System.out.println("getPostsWithFiltering: =" + posts);
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

        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        post.setImagePath(imagePath);
        List<Tag> tagList = validateTags(tags, post);
        post.setTags(tagList);

        Post savedPost = postRepository.saveOrUpdate(post);
        return new PostDto(savedPost);
    }

    public PostDto editPost(Long postId, String title, String text, MultipartFile image, String tags) {
        Post post = postRepository.findById(postId);

        String imagePath = fileStorageService.storeFile(image);
        if (imagePath != null) post.setImagePath(imagePath);

        post.setTitle(title);
        post.setText(text);
        List<Tag> tagsList = validateTags(tags, post);
        post.setTags(tagsList);

        Post savedPost = postRepository.saveOrUpdate(post);
        return new PostDto(savedPost);
    }

    public void managerLikesCount(Long postId, Boolean isLike) {
        if (isLike) {
            postRepository.incrementLikes(postId);
        } else {
            postRepository.decrementLikes(postId);
        }
    }

    public PostDto addCommentForPost(Long postId, String commentText) {
        if (commentText == null || commentText.isEmpty()) {
            throw new IllegalArgumentException("Comment text is null or empty");
        }
        Comment comment = new Comment();
        comment.setText(commentText);
        commentRepository.save(postId, comment);

        return new PostDto(postRepository.findById(postId));
    }

    public PostDto editCommentForPost(Long postId, Long commentId, String commentText) {
        if (commentText == null || commentText.isEmpty()) {
            throw new IllegalArgumentException("Comment text is null or empty");
        }
        commentRepository.update(commentId, commentText);
        Post post = postRepository.findById(postId);
        return new PostDto(post);
    }

    public PostDto deleteCommentForPost(Long postId, Long commentId) {
        commentRepository.delete(commentId);
        return new PostDto(postRepository.findById(postId));
    }

    public void deletePost(Long postId) {
        commentRepository.deleteByPostId(postId);
        tagRepository.deleteByPostId(postId);
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
