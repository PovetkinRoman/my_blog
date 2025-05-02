package ru.rpovetkin.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Post {

    private Long id;
    private String title;
    private String text;
    private String imagePath;
    private int likesCount;
    private List<String> comments = new ArrayList<>();

    public Post() {}

    public Post(Long id, String title, String text, String imagePath, int likesCount, List<String> comments) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.imagePath = imagePath;
        this.likesCount = likesCount;
        this.comments = comments;
    }
    public Post(Long id, String title, String text, String imagePath, int likesCount) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.imagePath = imagePath;
        this.likesCount = likesCount;
    }
}
