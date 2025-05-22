package ru.rpovetkin.repository;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.rpovetkin.dao.entity.Comment;
import ru.rpovetkin.dao.entity.Post;
import ru.rpovetkin.dao.entity.Tag;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> findAll(int page, int size) {
        // 1. Сначала получаем ID постов с пагинацией
        List<Long> postIds = jdbcTemplate.queryForList(
                "SELECT id FROM Post ORDER BY id LIMIT ? OFFSET ?",
                Long.class,
                size,
                page * size
        );

        if (postIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Собираем IN-условие для запросов
        String inClause = postIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // 3. Получаем основные данные постов
        Map<Long, Post> postMap = jdbcTemplate.query(
                "SELECT id, title, text, image_path, likes_count FROM Post WHERE id IN (" + inClause + ")",
                (rs, rowNum) -> new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("text"),
                        rs.getString("image_path"),
                        rs.getInt("likes_count")
                )
        ).stream().collect(Collectors.toMap(Post::getId, Function.identity()));

        // 4. Загружаем комментарии для этих постов
        jdbcTemplate.query(
                "SELECT id, post_id, text FROM Comment WHERE post_id IN (" + inClause + ")",
                rs -> {
                    Long postId = rs.getLong("post_id");
                    Post post = postMap.get(postId);
                    if (post != null) {
                        post.getComments().add(new Comment(
                                rs.getLong("id"),
                                post,
                                rs.getString("text")
                        ));
                    }
                }
        );

        // 5. Загружаем теги для этих постов
        jdbcTemplate.query(
                "SELECT id, post_id, name FROM Tag WHERE post_id IN (" + inClause + ")",
                rs -> {
                    Long postId = rs.getLong("post_id");
                    Post post = postMap.get(postId);
                    if (post != null) {
                        post.getTags().add(new Tag(
                                rs.getLong("id"),
                                post,
                                rs.getString("name")
                        ));
                    }
                }
        );

        // 6. Возвращаем посты в порядке их ID
        return postIds.stream()
                .map(postMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Post findById(Long postId) {
        String sql = """
                SELECT 
                    p.id AS post_id, p.title, p.text, p.image_path, p.likes_count,
                    c.id AS comment_id, c.text AS comment_text,
                    t.id AS tag_id, t.name AS tag_name
                FROM 
                    Post p
                LEFT JOIN 
                    Comment c ON p.id = c.post_id
                LEFT JOIN 
                    Tag t ON p.id = t.post_id
                WHERE 
                    p.id = ?
                """;

        // Используем Map для агрегации данных
        Map<Long, Post> postMap = new HashMap<>();

        jdbcTemplate.query(
                sql,
                rs -> {
                    Long currentPostId = rs.getLong("post_id");

                    Post post = postMap.computeIfAbsent(currentPostId, id -> {
                        Post newPost = new Post();
                        newPost.setId(id);
                        try {
                            newPost.setTitle(rs.getString("title"));
                            newPost.setText(rs.getString("text"));
                            newPost.setImagePath(rs.getString("image_path"));
                            newPost.setLikesCount(rs.getInt("likes_count"));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        newPost.setComments(new ArrayList<>());
                        newPost.setTags(new ArrayList<>());
                        return newPost;
                    });

                    long commentId = rs.getLong("comment_id");
                    if (commentId != 0L) {
                        Comment comment = new Comment();
                        comment.setId(commentId);
                        comment.setText(rs.getString("comment_text"));
                        comment.setPost(post);
                        post.getComments().add(comment);
                    }

                    long tagId = rs.getLong("tag_id");
                    if (tagId != 0L) {
                        Tag tag = new Tag();
                        tag.setId(tagId);
                        tag.setName(rs.getString("tag_name"));
                        tag.setPost(post); // Устанавливаем связь
                        post.getTags().add(tag);
                    }
                },
                postId
        );
        return postMap.get(postId);
    }

    @Override
    public void incrementLikes(Long postId) {
        jdbcTemplate.update("UPDATE Post SET likes_count = likes_count + 1 WHERE id = ?", postId);
    }

    @Override
    public void decrementLikes(Long postId) {
        jdbcTemplate.update("UPDATE Post SET likes_count = likes_count - 1 WHERE id = ?", postId);
    }

    @Override
    public void deleteById(Long postId) {
        jdbcTemplate.update(
                "DELETE FROM Post WHERE id = ?",
                postId
        );
    }

    @Transactional
    @Override
    public Post saveOrUpdate(Post post) {
        if (post.getId() != null) {
            return updatePost(post);
        } else {
            return createPost(post);
        }
    }

    private Post createPost(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Post (title, text, image_path, likes_count) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setString(3, post.getImagePath());
            ps.setInt(4, post.getLikesCount());
            return ps;
        }, keyHolder);

        Long postId = keyHolder.getKey().longValue();
        post.setId(postId);

        if (post.getTags() != null && !post.getTags().isEmpty()) {
            batchInsertTags(post.getTags(), postId);
        }
        return post;
    }

    private Post updatePost(Post post) {
        Long postId = post.getId();

        jdbcTemplate.update(
                "UPDATE Post SET title = ?, text = ?, image_path = ?, likes_count = ? WHERE id = ?",
                post.getTitle(),
                post.getText(),
                post.getImagePath(),
                post.getLikesCount(),
                postId
        );
        updateTags(post);
        updateComments(post);

        return post;
    }

    private void updateComments(Post post) {
        List<Comment> existingComments = post.getComments().stream().filter(c -> c.getId() != null).toList();
        for (Comment comment : existingComments) {
            jdbcTemplate.update(
                    "UPDATE Comment SET text = ? WHERE id = ?",
                    comment.getText(),
                    comment.getId()
            );
        }
        List<Comment> newComments = post.getComments().stream().filter(c -> c.getId() == null).toList();
        for (Comment comment : newComments) {
            jdbcTemplate.update(
                    "INSERT INTO Comment (post_id, text) VALUES (?, ?)",
                    post.getId(),
                    comment.getText()
            );
        }
    }

    private void updateTags(Post post) {
        List<Tag> existingTag = post.getTags().stream().filter(t -> t.getId() != null).toList();
        for (Tag tag : existingTag) {
            jdbcTemplate.update(
                    "UPDATE Tag SET name = ? WHERE id = ?",
                    tag.getName(),
                    tag.getId()
            );
        }
        List<Tag> newTag = post.getTags().stream().filter(t -> t.getId() == null).toList();
        for (Tag tag : newTag) {
            jdbcTemplate.update(
                    "INSERT INTO Tag (post_id, name) VALUES (?, ?)",
                    post.getId(),
                    tag.getName()
            );
        }
    }

    // Вспомогательный метод для пакетного сохранения тегов
    private void batchInsertTags(List<Tag> tags, Long postId) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO Tag (post_id, name) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Tag tag = tags.get(i);
                        ps.setLong(1, postId);
                        ps.setString(2, tag.getName());
                    }

                    @Override
                    public int getBatchSize() {
                        return tags.size();
                    }
                }
        );
    }
}
