package ru.rpovetkin.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {
    private Long id;
    private Post post;
    private String text;

    public Comment(Post post, String text) {
    }
    public Comment(Long id, String text) {
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", post=" + post.getId() +
                ", text='" + text + '\'' +
                '}';
    }
}
