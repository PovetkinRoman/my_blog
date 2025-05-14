package ru.rpovetkin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rpovetkin.repository.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
