package ru.rpovetkin.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcNativeTagRepository implements TagRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativeTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void deleteByPostId(Long postId) {
        jdbcTemplate.update(
                "DELETE FROM Tag WHERE post_id = ?",
                postId
        );
    }
}
