package ru.rpovetkin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rpovetkin.repository.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
