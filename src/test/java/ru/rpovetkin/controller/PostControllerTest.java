package ru.rpovetkin.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.rpovetkin.controller.model.PostDto;
import ru.rpovetkin.dao.entity.Post;
import ru.rpovetkin.service.FileStorageService;
import ru.rpovetkin.service.PostService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void getPostById_shouldReturnHtmlWithPost() throws Exception {
        Long postId = 1L;
        String expectedTitle = "title1";
        String expectedText = "This is a test post content";
        String expectedImagePath = "/images/test.jpg";
        int expectedLikesCount = 10;
        Post post = new Post(postId, expectedTitle, expectedText, "/images/test.jpg", expectedLikesCount);
        when(postService.getPost(anyLong())).thenReturn(new PostDto(post));

        MvcResult mvcResult = mockMvc.perform(get("/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post")).andReturn();

        PostDto actualPost = (PostDto) mvcResult.getModelAndView().getModel().get("post");
        assertEquals(postId, actualPost.id());
        assertEquals(expectedTitle, actualPost.title());
        assertEquals(expectedText, actualPost.text());
        assertEquals(expectedImagePath, actualPost.imagePath());
        assertEquals(expectedLikesCount, actualPost.likesCount());
        System.out.println(actualPost.comments());
        System.out.println(actualPost);
    }

    @Test
    void getAllPost_shouldReturnPostsWithPaging() throws Exception {
        mockMvc.perform(get("/posts")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts", "paging", "search"));
    }

    @Test
    void getAllPost_withSearchFilter_shouldReturnFilteredPosts() throws Exception {
        Long postId = 1L;
        String newTitle = "Updated Title";
        String newText = "Updated content";
        Post post = new Post(postId, newTitle, newText, "", 10);
        PostDto postDto = new PostDto(post);
        List<PostDto> listPostDto = new ArrayList<>();
        listPostDto.add(postDto);
        String searchTag = "linux";

        when(postService.getPostsWithFiltering(anyString(), any(), any())).thenReturn(listPostDto);
        MvcResult mvcResult = mockMvc.perform(get("/posts")
                        .param("search", searchTag)
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andReturn();
        List<PostDto> actualPost = (List<PostDto>) mvcResult.getModelAndView().getModel().get("posts");
        assertEquals("Updated Title", actualPost.get(0).title());
    }

    @Test
    void getAddPostPage_shouldReturnAddPostView() throws Exception {
        mockMvc.perform(get("/posts/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"));
    }

    @Test
    void createLike_shouldAddLikeAndRedirect() throws Exception {
        Long postId = 1L;

        mockMvc.perform(post("/posts/{id}/like", postId)
                        .param("like", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));
    }

    @Test
    void createLike_shouldRemoveLikeAndRedirect() throws Exception {
        Long postId = 2L;

        mockMvc.perform(post("/posts/{id}/like", postId)
                        .param("like", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));
    }

    @Test
    void editPost_shouldReturnEditFormWithPostData() throws Exception {
        Long postId = 1L;
        String newTitle = "Updated Title";
        String newText = "Updated content";
        Post post = new Post(postId, newTitle, newText, "", 10);

        when(postService.getPost(anyLong())).thenReturn(new PostDto(post));

        MvcResult mvcResult = mockMvc.perform(get("/posts/{id}/edit", postId))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"))
                .andExpect(model().attributeExists("post")).andReturn();

        PostDto actualPost = (PostDto) mvcResult.getModelAndView().getModel().get("post");
        assertEquals(postId, actualPost.id());
        assertEquals(newTitle, actualPost.title());
        assertEquals(newText, actualPost.text());
    }

    @Test
    void editPost_shouldUpdateExistingPostAndRedirect() throws Exception {
        Long postId = 2L;
        String newTitle = "Updated Title";
        String newText = "Updated content";
        String tags = "java,spring";
        Post post = new Post(postId, newTitle, newText, "", 10);
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());

        when(postService.editPost(postId, newTitle, newText, imageFile, tags)).thenReturn(new PostDto(post));
        mockMvc.perform(multipart("/posts/{id}", postId)
                        .file(imageFile)
                        .param("title", newTitle)
                        .param("text", newText)
                        .param("tags", tags))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));
        verify(postService).editPost(postId, newTitle, newText, imageFile, tags);
    }

    @Test
    void editPost_withEmptyTitle_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(multipart("/posts/1")
                        .param("title", "") // Пустой заголовок
                        .param("text", "Content")
                        .param("tags", "java"))
                .andExpect(status().isBadRequest());
    }
}
