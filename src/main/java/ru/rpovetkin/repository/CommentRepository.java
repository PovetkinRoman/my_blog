package ru.rpovetkin.repository;

import ru.rpovetkin.dao.entity.Comment;

public interface CommentRepository {
    void save(Long postId, Comment comment);
    void update(Long commentId, String text);
    void delete(Long commentId);
    void deleteByPostId(Long postId);
}