package ru.rpovetkin.controller.model;

import ru.rpovetkin.repository.entity.Post;

public record CommentDto(Long id, Post post, String text) {
}