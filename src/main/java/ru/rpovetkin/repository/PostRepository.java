package ru.rpovetkin.repository;

import ru.rpovetkin.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<Post> findAll();
    Optional<Post> findById(Long id);
}
