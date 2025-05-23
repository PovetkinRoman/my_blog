package ru.rpovetkin.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.rpovetkin.dao.entity.Comment;
import ru.rpovetkin.dao.entity.Post;
import ru.rpovetkin.dao.entity.Tag;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostRepositoryIntegrationTest {

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
        Post post1 = createPost(null, "title1", "text1", "1746438946080.jpg", 30, new ArrayList<>(), new ArrayList<>());
        postRepository.saveOrUpdate(post1);
        Post post2 = createPost(null, "title2", "text2", "1746438946080.jpg", 2, new ArrayList<>(), new ArrayList<>());
        postRepository.saveOrUpdate(post2);
        List<Post> all = postRepository.findAll(0, 10);

        assertNotNull(all);
        assertEquals(2, all.size());

        Post post = all.getFirst();
        assertEquals("title1", post.getTitle());
    }

    @Test
    void deleteById_shouldRemoveCommentFromDatabase() {
        Post post1 = createPost(null, "title1", "text1", "1746438946080.jpg", 30, new ArrayList<>(), new ArrayList<>());
        postRepository.saveOrUpdate(post1);
        Post post2 = createPost(null, "title2", "text2", "1746438946080.jpg", 2, new ArrayList<>(), new ArrayList<>());
        postRepository.saveOrUpdate(post2);
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
