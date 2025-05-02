package ru.rpovetkin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.rpovetkin.model.Paging;
import ru.rpovetkin.model.Post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    @GetMapping
    public String posts(Model model) {
        List<String> comments = new ArrayList<>();
        comments.add("first comment");
        comments.add("second comment");
        List<Post> posts = Arrays.asList(
                new Post(1L, "Title1", "TextPreview1", "path1", 1, comments),
                new Post(2L, "Title2", "TextPreview2", "path2", 2, comments),
                new Post(3L, "Title3", "TextPreview3", "path3", 3, comments)
        );

        Paging paging = new Paging(1, 5, false, false);
        // Передаём данные в виде атрибута posts
        model.addAttribute("posts", posts);
        model.addAttribute("search", "");
        model.addAttribute("paging", paging);

        return "posts"; // Возвращаем название шаблона — users.html
    }
}
