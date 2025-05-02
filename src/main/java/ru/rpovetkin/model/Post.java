package ru.rpovetkin.model;

import java.util.List;

public class Post {

    private Long id;
    private String title;
    private String text;
    private String imagePath;
    private int likesCount;
    private List<String> comments;

    public Post() {
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
