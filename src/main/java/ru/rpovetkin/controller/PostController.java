package ru.rpovetkin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rpovetkin.controller.model.PostDto;
import ru.rpovetkin.repository.entity.Paging;
import ru.rpovetkin.repository.entity.Tag;
import ru.rpovetkin.service.PostService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/posts")
public class PostController {

    private static final Logger log = Logger.getLogger(PostController.class.getName());

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping()
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

    @PostMapping
    public String createPost(
            @RequestParam("title") String title,
            @RequestParam("image") MultipartFile image,
            @RequestParam("tags") String tags,
            @RequestParam("text") String text,
            RedirectAttributes redirectAttributes) throws IOException {
        log.info("createPost: title=" + title);
        if (image == null || image.isEmpty()) {
            log.info("createPost: image is null or empty");
        } else {
            log.info("createPost: image name=" + image.getOriginalFilename());
        }
//         Преобразуем строку тегов в список
        List<Tag> tagList = Arrays.stream(tags.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .distinct()
                .map(s -> {
                    Tag tag = new Tag();
                    tag.setName(s);
                    return tag;
                })
                .collect(Collectors.toList());

        // Создаем пост
        PostDto post = postService.createPost(title, text, image, tagList);

//         Добавляем ID в redirect атрибуты
        redirectAttributes.addAttribute("id", post.id());
        return "redirect:/posts/{id}";
    }
}
