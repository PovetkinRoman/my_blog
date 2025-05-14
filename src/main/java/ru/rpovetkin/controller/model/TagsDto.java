package ru.rpovetkin.controller.model;

import ru.rpovetkin.repository.entity.Post;

public record TagsDto(Long id, Post post, String name) {
}