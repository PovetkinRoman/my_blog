package ru.rpovetkin.controller.model;

import org.springframework.web.multipart.MultipartFile;
import ru.rpovetkin.repository.entity.Post;

import java.util.List;
import java.util.stream.Collectors;

public record PostDto(Long id, String title, String text, String imagePath, Integer likesCount,
                      List<CommentDto> comments, List<TagsDto> tags, String tagsAsText, MultipartFile image) {
    public PostDto(Post post) {
        this(
                post.getId(),
                post.getTitle(),
                post.getText(),
                post.getImagePath(),
                post.getLikesCount(),
                post.getComments().stream()
                        .map(comment -> new CommentDto(comment.getId(), comment.getPost(), comment.getText()))
                        .toList(),
                post.getTags().stream()
                        .map(tag -> new TagsDto(tag.getId(), tag.getPost(), tag.getName()))
                        .toList(),
                post.getTags().stream()
                        .map(tag -> "#" + tag.getName())
                        .collect(Collectors.joining(" ")),
                null
        );
    }
}