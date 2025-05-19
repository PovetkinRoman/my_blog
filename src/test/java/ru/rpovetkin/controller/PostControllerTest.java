package ru.rpovetkin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.rpovetkin.config.DataSourceTestConfiguration;
import ru.rpovetkin.config.WebTestConfiguration;
import ru.rpovetkin.controller.model.PostDto;
import ru.rpovetkin.repository.JdbcNativePostRepository;
import ru.rpovetkin.service.FileStorageService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {DataSourceTestConfiguration.class, WebTestConfiguration.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
class PostControllerTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcNativePostRepository jdbcNativePostRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        jdbcTemplate.execute("DELETE FROM Comment");
        jdbcTemplate.execute("DELETE FROM Tag");
        jdbcTemplate.execute("DELETE FROM Post");

        jdbcTemplate.execute("insert into Post(id, title, text, image_path, likes_count) values (1, 'title1', 'This is a test post content', '/images/test.jpg', 10)");
        jdbcTemplate.execute("insert into Post(id, title, text, image_path, likes_count) values (2, 'Title 2', 'Text 2', '/img2.jpg', 20)");
    }

    @Test
    void getPostById_shouldReturnHtmlWithPost() throws Exception {
        Long postId = 1L;
        String expectedTitle = "title1";
        String expectedText = "This is a test post content";
        String expectedImagePath = "/images/test.jpg";
        int expectedLikesCount = 10;

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
                .andExpect(model().attributeExists("posts", "paging", "search"))
                .andExpect(model().attribute("search", isEmptyString()));
    }

    @Test
    void getAllPost_withSearchFilter_shouldReturnFilteredPosts() throws Exception {
        jdbcTemplate.execute("insert into Tag(id, post_id, name) values (1, 1, 'tags1Forpost_id1')");

        MvcResult mvcResult = mockMvc.perform(get("/posts")
                        .param("search", "tags1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("posts", hasSize(1)))
                .andReturn();
        List<PostDto> actualPost = (List<PostDto>) mvcResult.getModelAndView().getModel().get("posts");
        assertEquals("title1", actualPost.get(0).title());
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
        Long postId = jdbcNativePostRepository.findAll(0, 10).get(0).getId();
        String text = "This is a test post content";
        String title = "title1";

        MvcResult mvcResult = mockMvc.perform(get("/posts/{id}/edit", postId))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"))
                .andExpect(model().attributeExists("post")).andReturn();

        PostDto actualPost = (PostDto) mvcResult.getModelAndView().getModel().get("post");
        assertEquals(postId, actualPost.id());
        assertEquals(title, actualPost.title());
        assertEquals(text, actualPost.text());
    }

    @Test
    void editPost_shouldUpdateExistingPostAndRedirect() throws Exception {
        Long postId = 2L;
        String newTitle = "Updated Title";
        String newText = "Updated content";
        String tags = "java,spring";
        String storedFilename = "updated_image.jpg";

        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());
        when(fileStorageService.storeFile(imageFile)).thenReturn(storedFilename);

        mockMvc.perform(multipart("/posts/{id}", postId)
                        .file(imageFile)
                        .param("title", newTitle)
                        .param("text", newText)
                        .param("tags", tags))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));
        verify(fileStorageService).storeFile(imageFile);
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
