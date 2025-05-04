package ru.rpovetkin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.rpovetkin.controller.model.PostDto;
import ru.rpovetkin.repository.entity.Paging;
import ru.rpovetkin.service.PostService;

import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/posts")
public class PostController {

    private static final Logger log = Logger.getLogger(PostController.class.getName());


    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String getAllPost(Model model) {
        List<PostDto> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);

        Paging paging = new Paging(1, 5, false, false);
        model.addAttribute("paging", paging);

        model.addAttribute("search", "");
        return "posts";
    }

    @GetMapping(value = "/{id}")
    public String post(@PathVariable(name = "id") Long id, Model model) {
        PostDto post = postService.getPost(id);
        model.addAttribute("post", post);
        return "post";
    }

    @GetMapping(value = "/add")
    public String post(Model model) {
        return "add-post";
    }

//    @PostMapping
//    public String save(@ModelAttribute Post post) {
//        postService.save(post);
//        return "redirect:/posts"; // Возвращаем страницу, чтобы она перезагрузилась
//    }


}
