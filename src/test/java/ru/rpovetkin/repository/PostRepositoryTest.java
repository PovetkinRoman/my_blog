package ru.rpovetkin.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.rpovetkin.config.DataSourceTestConfiguration;
import ru.rpovetkin.dao.entity.Comment;
import ru.rpovetkin.dao.entity.Post;
import ru.rpovetkin.dao.entity.Tag;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {DataSourceTestConfiguration.class,
        JdbcNativePostRepository.class,
        JdbcNativeCommentRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM Comment");
        jdbcTemplate.execute("DELETE FROM Tag");
        jdbcTemplate.execute("DELETE FROM Post");

        jdbcTemplate.execute("insert into Post(id, title, text, image_path, likes_count) values (1, 'title1', 'This is a test post content', '/images/test.jpg', 10)");
        jdbcTemplate.execute("insert into Post(id, title, text, image_path, likes_count) values (2, 'Title 2', 'Text 2', '/img2.jpg', 20)");
        jdbcTemplate.execute("insert into Comment(id, post_id, text) values (1, 1, 'comments1Forpost_id1')");
        jdbcTemplate.execute("insert into Comment(id, post_id, text) values (2, 2, 'comments1Forpost_id2')");
        jdbcTemplate.execute("insert into Comment(id, post_id, text) values (3, 2, 'comments2Forpost_id2')");
        jdbcTemplate.execute("insert into Tag(id, post_id, name) values (1, 1, 'tags1Forpost_id1')");
        jdbcTemplate.execute("insert into Tag(id, post_id, name) values (2, 2, 'tags1Forpost_id2')");
        jdbcTemplate.execute("insert into Tag(id, post_id, name) values (3, 2, 'tags1Forpost_id2')");
    }

    @Test
    void save_shouldAddPostToDatabase() {
        Post post = createPost(4L, "titleTest1", "textTest1", "/imagePathTest1", 3, new ArrayList<>(), new ArrayList<>());

        Post savedPost = postRepository.saveOrUpdate(post);

        assertNotNull(savedPost);
        assertEquals("titleTest1", savedPost.getTitle());
        assertEquals("textTest1", savedPost.getText());
    }

    @Test
    void findAll_shouldReturnAllPost() {
        List<Post> all = postRepository.findAll(0, 10);

        assertNotNull(all);
        assertEquals(2, all.size());

        Post post = all.getFirst();
        assertEquals("title1", post.getTitle());
    }

    @Test
    void deleteById_shouldRemoveCommentFromDatabase() {
        Long postId = postRepository.findAll(0, 10).getFirst().getId();
        commentRepository.deleteByPostId(postId);
        Post post = postRepository.findById(postId);
        assertTrue(post.getComments().isEmpty());
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
