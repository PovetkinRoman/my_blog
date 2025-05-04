package ru.rpovetkin.controller.model;

import ru.rpovetkin.repository.entity.Post;

import java.util.List;

public record PostDto(Long id, String title, String text, String imagePath, Integer likesCount,
                      List<CommentDto> comments) {
    public PostDto(Post post) {
        this(
                post.getId(),
                post.getTitle(),
                post.getText(),
                post.getImagePath(),
                post.getLikesCount(),
                post.getComments().stream()
                        .map(comment -> new CommentDto(comment.getId(), comment.getPost(), comment.getText()))
                        .toList()
        );
    }
}