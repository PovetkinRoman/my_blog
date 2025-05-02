package ru.rpovetkin.repository;

import ru.rpovetkin.model.Post;

import java.util.List;

public interface PostRepository {
    List<Post> findAll();
}
