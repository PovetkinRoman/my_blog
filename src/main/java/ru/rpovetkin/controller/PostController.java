package ru.rpovetkin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.rpovetkin.model.Paging;
import ru.rpovetkin.model.Post;
import ru.rpovetkin.service.PostService;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String posts(Model model) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);

        Paging paging = new Paging(1, 5, false, false);
        model.addAttribute("paging", paging);

        model.addAttribute("search", "");
        return "posts";
    }

    @GetMapping(value = "/{id}")
    public String post(@PathVariable(name = "id") Long id, Model model) {
        Post post = postService.getPost(id).get();
        model.addAttribute("post", post);
        return "post";
    }

    @GetMapping(value = "/add")
    public String post(Model model) {
        return "add-post";
    }
}
