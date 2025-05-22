package ru.rpovetkin.repository;

import ru.rpovetkin.dao.entity.Post;

import java.util.List;

public interface PostRepository {
    List<Post> findAll(int page, int size);

    Post findById(Long postId);

    Post saveOrUpdate(Post post);

    void incrementLikes(Long postId);

    void decrementLikes(Long postId);

    void deleteById(Long postId);
}