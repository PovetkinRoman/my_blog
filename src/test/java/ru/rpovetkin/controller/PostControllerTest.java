package ru.rpovetkin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.rpovetkin.config.JpaConfiguration;
import ru.rpovetkin.config.WebConfiguration;
import ru.rpovetkin.controller.model.PostDto;
import ru.rpovetkin.repository.PostRepository;
import ru.rpovetkin.repository.entity.Comment;
import ru.rpovetkin.repository.entity.Post;
import ru.rpovetkin.repository.entity.Tag;
import ru.rpovetkin.service.PostService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {JpaConfiguration.class, WebConfiguration.class
//        PostController.class,
//        PostRepository.class,
//        CommentRepository.class,
//        TagRepository.class,
//        FileStorageService.class,
//        PostService.class
})
@WebAppConfiguration
class PostControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PostService postService;

    @Autowired
    @Qualifier("postRepository")
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getUsers_shouldReturnHtmlWithUsers() throws Exception {
        Post post4 = createPost("Title 3", "text 3", "/image3", 4, new ArrayList<>(), new ArrayList<>());
        post4 = postRepository.save(post4);
        postRepository.flush();
        System.out.println(post4);

        MvcResult mvcResult = mockMvc.perform(get("/posts/" + post4.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post")).andReturn();
        PostDto post = (PostDto) mvcResult.getModelAndView().getModel().get("post");
        System.out.println(post.id()); // post.id null
    }

//    @Test
//    void getAllPost_shouldReturnDefaultPage() throws Exception {
//        Post post3 = createPost("Title 3", "text 3", "/image3", 4, new ArrayList<>(), new ArrayList<>());
//
//
//        List<PostDto> mockPosts = List.of(
//                new PostDto(post3)
//        );
//
//        when(postService.getPostsWithFiltering("", 0, 10))
//                .thenReturn(mockPosts);
//
//        mockMvc.perform(get("/posts"))
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("posts", mockPosts));
//    }

//    @Test
//    void getPostById_shouldReturnPostView() throws Exception {
//        mockMvc.perform(get("/posts/{id}", 1L))
//                .andExpect(status().isOk())
//                .andExpect(view().name("post"))
//                .andExpect(model().attributeExists("post"));
//    }

//    @Test
//    void getAllPost_shouldReturnDefaultPage() throws Exception {
//        List<Post> posts = initPosts();
//        List<PostDto> mockPosts = Arrays.asList(
//                new PostDto(posts.get(0)),
//                new PostDto(posts.get(1))
//        );
//
//        when(postService.getPostsWithFiltering(anyString(), anyInt(), anyInt()))
//                .thenReturn(mockPosts);
//
//        mockMvc.perform(get("/posts")
//                        .param("pageNumber", "2")
//                        .param("pageSize", "5"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("posts"))
//                .andExpect(model().attributeExists("posts"))
//                .andExpect(model().attributeExists("paging"))
//                .andExpect(model().attributeExists("search"))
//                .andExpect(model().attribute("posts", mockPosts));
//    }


//    private List<Post> initPosts() {
//        Post post1 = createPost("Title 1", "text 1", "/image1", 2, new ArrayList<>(), null);
//        Post post2 = createPost("Title 2", "text 2", "/image2", 3, null, null);
//        Post post3 = createPost("Title 3", "text 3", "/image3", 4, new ArrayList<>(), new ArrayList<>());
//
//        post1.setTags(Arrays.asList(
//                new Tag(null, post1, "database"),
//                new Tag(null, post1, "nosql")
//        ));
//
//        post2.setComments(Arrays.asList(
//                new Comment(null, post2, "java is cool"),
//                new Comment(null, post2, "linux is so cool")
//        ));
//
//        post2.setTags(Arrays.asList(
//                new Tag(null, post2, "java"),
//                new Tag(null, post2, "linux")
//        ));
//
//        List<Post> posts = postRepository.saveAll(Arrays.asList(post1, post2, post3));
//        System.out.println("posts: " + posts);
//        return posts;
//    }

    private Post createPost(String title, String text, String imagePath, int likesCount,
                            List<Comment> comments, List<Tag> tags) {
        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        post.setImagePath(imagePath);
        post.setLikesCount(likesCount);
        post.setComments(comments);
        post.setTags(tags);
        return post;
    }
}
