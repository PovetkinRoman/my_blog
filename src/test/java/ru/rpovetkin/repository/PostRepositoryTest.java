package ru.rpovetkin.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.rpovetkin.config.DataSourceConfiguration;
import ru.rpovetkin.repository.entity.Comment;
import ru.rpovetkin.repository.entity.Post;
import ru.rpovetkin.repository.entity.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, PostRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        initPosts();
    }

    @Test
    void save_shouldAddPostToDatabase() {
        Post post = createPost("titleTest1", "textTest1", "/imagePathTest1", 3, new ArrayList<>(), new ArrayList<>());

        Post savedPost = postRepository.save(post);

        assertNotNull(savedPost);
        assertEquals("titleTest1", savedPost.getTitle());
        assertEquals("textTest1", savedPost.getText());
    }

    @Test
    void findAll_shouldReturnAllPost() {
        List<Post> posts = postRepository.findAll();

        assertNotNull(posts);
        assertEquals(3, posts.size());

        Post post = posts.getFirst();
        System.out.println(post.getText());
        assertEquals(1L, post.getId());
        assertEquals("Title 1", post.getTitle());
    }

    @Test
    void deleteById_shouldRemoveUserFromDatabase() {
        postRepository.deleteById(1L);

        List<Post> users = postRepository.findAll();

        Post deletedUser = users.stream()
                .filter(createdUsers -> createdUsers.getId().equals(1L))
                .findFirst()
                .orElse(null);
        assertNull(deletedUser);
    }


    private List<Post> initPosts() {
        Post post1 = createPost("Title 1", "text 1", "/image1", 2, new ArrayList<>(), null);
        Post post2 = createPost("Title 2", "text 2", "/image2", 3, null, null);
        Post post3 = createPost("Title 3", "text 3", "/image3", 4, new ArrayList<>(), new ArrayList<>());

        // Add tags after posts are created
        post1.setTags(Arrays.asList(
                new Tag(null, post1, "database"),
                new Tag(null, post1, "nosql")
        ));

        post2.setComments(Arrays.asList(
                new Comment(null, post2, "java is cool"),
                new Comment(null, post2, "linux is so cool")
        ));

        post2.setTags(Arrays.asList(
                new Tag(null, post2, "java"),
                new Tag(null, post2, "linux")
        ));

        return postRepository.saveAll(Arrays.asList(post1, post2, post3));
    }

    private Post createPost(String title, String text, String imagePath, int likesCount,
                            List<Comment> comments, List<Tag> tags) {
        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        post.setImagePath(imagePath);
        post.setLikesCount(likesCount);
        post.setComments(comments);
        post.setTags(tags);
        return post;
    }
}
