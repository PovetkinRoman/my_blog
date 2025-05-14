package ru.rpovetkin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.rpovetkin.config.TestConfig;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = TestConfig.class)
class PostServiceTest {
    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostService postService;

    @Autowired
    FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        Mockito.reset(postRepository);
        Mockito.reset(fileStorageService);
    }

    @Test
    void testPostFindByIdSuccess() {
        List<Post> expectedPosts = initPosts();

        when(postRepository.findById(1L)).thenReturn(Optional.of(expectedPosts.get(0)));
        PostDto result = postService.getPost(1L);

        assertEquals(1L, result.id());
        assertEquals("Title 1", result.title());
        assertEquals("text 1", result.text());
        assertEquals("/image1", result.imagePath());

        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void testPostNotFound() {

        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        PostDto result = postService.getPost(1L);
        assertNull(result.id());

    }

    @Test
    public void getPostsWithFiltering_NoSearchTerm_ReturnsAllPosts() {
        List<Post> expectedPosts = initPosts();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> page = new PageImpl<>(expectedPosts);

        when(postRepository.findAll(any(Pageable.class))).thenReturn(page);
        List<PostDto> result = postService.getPostsWithFiltering("", 0, 10);

        assertEquals(3, result.size());
        verify(postRepository, times(1)).findAll(pageable);
    }

    @Test
    public void getPostsWithFiltering_WithSearchTerm_ReturnsFilteredPosts() {
        List<Post> allPosts = initPosts();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> page = new PageImpl<>(allPosts);

        when(postRepository.findAll(any(Pageable.class))).thenReturn(page);
        List<PostDto> result = postService.getPostsWithFiltering("java", 0, 10);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).id());
        verify(postRepository, times(1)).findAll(pageable);
    }

    @Test
    public void getPostsWithFiltering_WithSearchTermNoMatches_ReturnsEmptyList() {
        List<Post> allPosts = initPosts();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> page = new PageImpl<>(allPosts);

        when(postRepository.findAll(any(Pageable.class))).thenReturn(page);
        List<PostDto> result = postService.getPostsWithFiltering("python", 0, 10);

        assertTrue(result.isEmpty());
        verify(postRepository, times(1)).findAll(pageable);
    }

    @Test
    public void getPostsWithFiltering_EmptySearchTerm_ReturnsAllPosts() {
        List<Post> expectedPosts = initPosts();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> page = new PageImpl<>(expectedPosts);

        when(postRepository.findAll(any(Pageable.class))).thenReturn(page);
        List<PostDto> result = postService.getPostsWithFiltering("", 0, 10);

        assertEquals(3, result.size());
        verify(postRepository, times(1)).findAll(pageable);
    }

    @Test
    public void createPost_WithValidData_ReturnsPostDto() {
        List<Post> posts = initPosts();

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(fileStorageService.storeFile(any(MultipartFile.class))).thenReturn("");
        when(postRepository.save(any(Post.class))).thenReturn(posts.get(0));

        PostDto result = postService.createPost("", "", image, "");

        assertNotNull(result);
        assertEquals("Title 1", result.title());
        assertEquals("text 1", result.text());
        assertEquals("/image1", result.imagePath());
        assertEquals(2, result.tags().size());

        verify(fileStorageService, times(1)).storeFile(image);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void createPost_WithEmptyTags_ReturnsPostDtoWithEmptyTags() throws Exception {
        List<Post> posts = initPosts();

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(fileStorageService.storeFile(any(MultipartFile.class))).thenReturn("");
        when(postRepository.save(any(Post.class))).thenReturn(posts.get(2));

        PostDto result = postService.createPost("", "", image, "");

        assertNotNull(result);
        assertTrue(result.tags().isEmpty());
    }

    @Test
    public void createPost_WithNullImage_ThrowsException() {
        when(fileStorageService.storeFile(null)).thenCallRealMethod();

        assertThrows(IllegalArgumentException.class, () -> {
            postService.createPost("", "", null, "");
        });
    }

    @Test
    public void editPost_WithValidData_ReturnsUpdatedPostDto() throws IOException {
        List<Post> posts = initPosts();

        String newImagePath = "/image2";

        MockMultipartFile newImage = new MockMultipartFile(
                "image",
                "image2.jpg",
                "image/jpeg",
                "updated image content".getBytes()
        );

        Post existingPost = posts.get(0);
        Post updatedPost = posts.get(1);

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(existingPost));
        when(fileStorageService.storeFile(newImage)).thenReturn(newImagePath);
        when(postRepository.save(existingPost)).thenReturn(updatedPost);

        PostDto result = postService.editPost(updatedPost.getId(), updatedPost.getTitle(), updatedPost.getText(),
                newImage, "");

        assertNotNull(result);
        assertEquals(updatedPost.getId(), result.id());
        assertEquals(updatedPost.getTitle(), result.title());
        assertEquals(updatedPost.getText(), result.text());
        assertEquals(newImagePath, result.imagePath());
        assertEquals(2, result.tags().size());

        verify(postRepository, times(1)).findById(updatedPost.getId());
        verify(fileStorageService, times(1)).storeFile(newImage);
        verify(postRepository, times(1)).save(existingPost);
    }

    @Test
    public void editPost_WithoutNewImage_KeepsOriginalImage() throws IOException {
        List<Post> posts = initPosts();
        Post existingPost = posts.get(0);

        String originalImagePath = "/image1";
        MultipartFile nullImage = null;


        when(postRepository.findById(existingPost.getId())).thenReturn(Optional.of(existingPost));
        when(fileStorageService.storeFile(any(MultipartFile.class))).thenReturn(originalImagePath);
        when(postRepository.save(existingPost)).thenReturn(existingPost);

        PostDto result = postService.editPost(existingPost.getId(), "Title", "Text", nullImage, "tags");

        assertEquals(originalImagePath, result.imagePath());
        verify(fileStorageService, times(1)).storeFile(any());
    }

    @Test
    public void managerLikesCount_WithLikeTrue_IncrementsLikesCount() {
        Post post = initPosts().get(0); //likes count 2

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto result = postService.managerLikesCount(post.getId(), true);

        assertEquals(3, result.likesCount());
        verify(postRepository, times(1)).findById(post.getId());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void managerLikesCount_WithLikeFalse_DecrementsLikesCount() {
        Post post = initPosts().get(0); //likes count 2

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto result = postService.managerLikesCount(post.getId(), false);

        assertEquals(1, result.likesCount());
        verify(postRepository, times(1)).findById(post.getId());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void addCommentForPost_WithValidData_AddsCommentAndReturnsPostDto() {
        Post post = initPosts().get(0); //comments count 0

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto result = postService.addCommentForPost(post.getId(), "new comment");

        assertNotNull(result);
        assertEquals(1, result.comments().size());
        assertEquals("new comment", result.comments().get(0).text());

        verify(postRepository, times(1)).findById(post.getId());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void addCommentForPost_WithEmptyCommentText_ThrowsException() {
        Post post = initPosts().get(0); //comments count 0

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class, () -> {
            postService.addCommentForPost(post.getId(), "");
        });

        verify(postRepository, never()).save(any());
    }

    @Test
    public void addCommentForPost_WithNullCommentText_ThrowsException() {
        Post post = initPosts().get(0); //comments count 0

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class, () -> {
            postService.addCommentForPost(post.getId(), null);
        });

        verify(postRepository, never()).save(any());
    }

    @Test
    public void addCommentForPost_ToPostWithExistingComments_PreservesOtherComments() {
        Long postId = 1L;
        String existingCommentText = "Existing comment";
        String newCommentText = "New comment";

        Comment existingComment = new Comment();
        existingComment.setText(existingCommentText);

        List<Comment> comments = new ArrayList<>();
        comments.add(existingComment);

        Post post = new Post();
        post.setId(postId);
        post.setComments(comments);

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto result = postService.addCommentForPost(post.getId(), newCommentText);

        assertEquals(2, result.comments().size());
        assertTrue(result.comments().stream().anyMatch(c -> c.text().equals(existingCommentText)));
        assertTrue(result.comments().stream().anyMatch(c -> c.text().equals(newCommentText)));
    }

    @Test
    public void editCommentForPost_WithValidData_UpdatesCommentAndReturnsPostDto() {
        Long postId = 1L;
        Long commentId = 1L;
        String newCommentText = "Updated comment text";

        Comment existingComment = new Comment();
        existingComment.setId(commentId);
        existingComment.setText("Old comment text");

        Post post = new Post();
        post.setId(postId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        PostDto result = postService.editCommentForPost(postId, commentId, newCommentText);

        assertNotNull(result);
        assertEquals(postId, result.id());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(existingComment);
        verify(postRepository, times(1)).findById(postId);
        assertEquals(newCommentText, existingComment.getText());
    }

    @Test
    public void editCommentForPost_WithEmptyCommentText_ThrowsException() {
        Long postId = 1L;
        Long commentId = 1L;

        Comment existingComment = new Comment();
        existingComment.setId(commentId);
        existingComment.setText("Old comment text");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        assertThrows(IllegalArgumentException.class, () -> {
            postService.editCommentForPost(postId, commentId, "");
        });

        verify(commentRepository, never()).save(any());
    }

    private List<Post> initPosts() {
        return Arrays.asList(
                createPost(1L, "Title 1", "text 1", "/image1", 2, new ArrayList<>(),
                        Arrays.asList(new Tag(5L, new Post(), "database"), new Tag(6L, new Post(), "nosql"))),

                createPost(2L, "Title 2", "text 2", "/image2", 3,
                        Arrays.asList(new Comment(5L, new Post(), "java is cool"), new Comment(6L, new Post(), "linux is so cool")),
                        Arrays.asList(new Tag(6L, new Post(), "java"), new Tag(7L, new Post(), "linux"))),

                createPost(3L, "Title 3", "text 3", "/image3", 4, new ArrayList<>(), new ArrayList<>())
        );
    }

    private Post createPost(Long id, String title, String text, String imagePath, int likesCount,
                            List<Comment> comments, List<Tag> tags) {
        Post post = new Post();
        post.setId(id);
        post.setTitle(title);
        post.setText(text);
        post.setImagePath(imagePath);
        post.setLikesCount(likesCount);
        post.setComments(comments);
        post.setTags(tags);
        return post;
    }
}