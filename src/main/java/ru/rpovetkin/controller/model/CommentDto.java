package ru.rpovetkin.controller.model;

import ru.rpovetkin.dao.entity.Post;

public record CommentDto(Long id, Post post, String text) {
}