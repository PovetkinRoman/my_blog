package ru.rpovetkin.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    private Long id;
    private Post post;
    private String name;

    public Tag(Post post, String name) {}

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", post=" + post.getId() +
                ", name='" + name + '\'' +
                '}';
    }
}
