package ru.rpovetkin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rpovetkin.controller.model.PostDto;
import ru.rpovetkin.repository.entity.Paging;
import ru.rpovetkin.service.PostService;

import java.io.IOException;
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
    public String getAllPost(
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            Model model) {
        List<PostDto> posts = postService.getPostsWithFiltering(search, pageNumber-1, pageSize);
        model.addAttribute("posts", posts);

        Paging paging = new Paging(pageNumber, pageSize, false, false);
        model.addAttribute("paging", paging);
        model.addAttribute("search", search);
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
            RedirectAttributes redirectAttributes) {

        // Создаем пост
        PostDto post = postService.createPost(title, text, image, tags);

//         Добавляем ID в redirect атрибуты
        redirectAttributes.addAttribute("id", post.id());
        return "redirect:/posts/{id}";
    }

    @PostMapping(value = "/{id}/like")
    public String createLike(
            @PathVariable("id") Long postId,
            @RequestParam("like") Boolean isLike,
            RedirectAttributes redirectAttributes) {

        PostDto post = postService.managerLikesCount(postId, isLike);
        redirectAttributes.addAttribute("id", post.id());
        return "redirect:/posts/{id}";
    }

    @GetMapping(value = "/{id}/edit")
    public String editPost(
            @PathVariable("id") Long postId,
            Model model,
            RedirectAttributes redirectAttributes) {
        PostDto post = postService.getPost(postId);
        model.addAttribute("post", post);
        redirectAttributes.addAttribute("id", post.id());
        return "add-post";
    }

    @PostMapping("/{id}")
    public String createPostWithId(
            @PathVariable("id") Long postId,
            @RequestParam("title") String title,
            @RequestParam("image") MultipartFile image,
            @RequestParam("tags") String tags,
            @RequestParam("text") String text,
            RedirectAttributes redirectAttributes) throws IOException {
        // Создаем пост
        PostDto post = postService.editPost(postId, title, text, image, tags);

//         Добавляем ID в redirect атрибуты
        redirectAttributes.addAttribute("id", post.id());
        return "redirect:/posts/{id}";
    }

    @PostMapping(value = "/{id}/comments")
    public String addCommentForPost(
            @PathVariable("id") Long postId,
            @RequestParam("text") String comment,
            Model model,
            RedirectAttributes redirectAttributes) {
        PostDto post = postService.addCommentForPost(postId, comment);
        model.addAttribute("post", post);
        redirectAttributes.addAttribute("id", post.id());
        return "redirect:/posts/{id}";
    }

    @PostMapping(value = "{id}/comments/{commentId}")
    public String editCommentForPost(
            @PathVariable("id") Long postId,
            @PathVariable("commentId") Long commentId,
            @RequestParam("text") String comment,
            Model model,
            RedirectAttributes redirectAttributes) {
        PostDto post = postService.editCommentForPost(postId, commentId, comment);
        model.addAttribute("post", post);
        redirectAttributes.addAttribute("id", post.id());
        return "redirect:/posts/{id}";
    }

    @PostMapping(value = "{id}/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable("id") Long postId,
            @PathVariable("commentId") Long commentId,
            Model model,
            RedirectAttributes redirectAttributes) {
        PostDto post = postService.deleteCommentForPost(postId, commentId);
        model.addAttribute("post", post);
        redirectAttributes.addAttribute("id", post.id());
        return "redirect:/posts/{id}";
    }

    @PostMapping(value = "{id}/delete")
    public String deletePost(
            @PathVariable("id") Long postId) {
        postService.deletePost(postId);
        return "redirect:/posts";
    }
}
