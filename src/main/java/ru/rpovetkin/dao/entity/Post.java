package ru.rpovetkin.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Post {

    private Long id;
    private String title;
    private String text;
    private String imagePath;
    private int likesCount;

    private List<Comment> comments = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();


    public Post(Long id, String title, String text, String imagePath, int likesCount) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.imagePath = imagePath;
        this.likesCount = likesCount;
    }
}
