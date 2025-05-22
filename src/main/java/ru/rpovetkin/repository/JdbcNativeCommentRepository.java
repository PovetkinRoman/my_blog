package ru.rpovetkin.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.rpovetkin.dao.entity.Comment;

@Repository
public class JdbcNativeCommentRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativeCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Long postId, Comment comment) {
        jdbcTemplate.update(
                "INSERT INTO Comment (post_id, text) VALUES (?, ?)",
                postId,
                comment.getText()
        );
    }

    @Override
    public void update(Long commentId, String text) {
        jdbcTemplate.update(
                "UPDATE Comment SET text = ? WHERE id = ?",
                text,
                commentId
        );
    }

    @Override
    public void delete(Long commentId) {
        jdbcTemplate.update(
                "DELETE FROM Comment WHERE id = ?",
                commentId
        );
    }

    @Override
    public void deleteByPostId(Long postId) {
        jdbcTemplate.update(
                "DELETE FROM Comment WHERE post_id = ?",
                postId
        );
    }
}
