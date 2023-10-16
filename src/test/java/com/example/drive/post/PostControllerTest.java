package com.example.drive.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostRepository postRepository;

    List<Post> posts = new ArrayList<>();

    @BeforeEach
    void setup() {
        posts = List.of(
                new Post(1, 1, "Hello", "Welcome", null),
                new Post(2, 2, "Welcome", "Hello", null)
        );
    }

    @Test
    void shouldFindAllPosts() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id": 1,
                        "userId": 1,
                        "title": "Hello",
                        "body": "Welcome",
                        "version": null
                    },
                    {
                        "id": 2,
                        "userId": 2,
                        "title": "Welcome",
                        "body": "Hello",
                        "version": null
                    }
                ]
                """;


        when(postRepository.findAll()).thenReturn(posts);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldFindPostWhenGivenValidId() throws Exception {
        when(postRepository.findById(1)).thenReturn(Optional.of(posts.get(0)));

        var post = posts.get(0);
        var jsonResponse =
                "{\n" +
                    "\"id\":" + post.id() + ",\n" +
                    "\"userId\":" + post.userId() + ",\n" + 
                    "\"title\":" + post.title() + ",\n" +
                    "\"body\":" + post.body() + ",\n" +
                    "\"version\": null\n" +
                "}";

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldNotFindPostWhenGivenInvalidId() throws Exception {
        when(postRepository.findById(999)).thenThrow(PostNotFoundException.class);

        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewPost() throws Exception {

        var post = new Post(3, 3, "Ali", "Ali", null);
        when(postRepository.save(post)).thenReturn(post);

        var jsonRequest =
                "{\n" +
                        "\"id\":" + post.id() + ",\n" +
                        "\"userId\":" + post.userId() + ",\n" +
                        "\"title\":" + "\"" + post.title() + "\"" + ",\n" +
                        "\"body\":" + "\"" + post.body() + "\"" + ",\n" +
                        "\"version\": null\n" +
                "}";

        mockMvc.perform(post("/api/posts")
                        .content("application/json")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotCreateNewPost() throws Exception {
        var post = new Post(4, 4, "", "", null);
        when(postRepository.save(post)).thenReturn(post);

        var jsonRequest =
                "{\n" +
                        "\"id\":" + post.id() + ",\n" +
                        "\"userId\":" + post.userId() + ",\n" +
                        "\"title\":" + "\"" + post.title() + "\"" + ",\n" +
                        "\"body\":" + "\"" + post.body() + "\"" + ",\n" +
                        "\"version\": null\n" +
                "}";

        mockMvc.perform(post("/api/posts")
                        .content("application/json")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdatePostIfValid() throws Exception {
        Post post = new Post(1, 1, "New Title", "New Body", 1);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        var jsonRequest =
                "{\n" +
                        "\"id\":" + post.id() + ",\n" +
                        "\"userId\":" + post.userId() + ",\n" +
                        "\"title\":" + "\"" + post.title() + "\"" + ",\n" +
                        "\"body\":" + "\"" + post.body() + "\"" + ",\n" +
                        "\"version\": null\n" +
                "}";

        mockMvc.perform(put("/api/posts/1")
                    .content("application/json")
                    .contentType("application/json")
                    .content(jsonRequest))
                .andExpect(status().isAccepted());
    }

    @Test
    void shouldDeletePostById() throws Exception {
        doNothing().when(postRepository).deleteById(1);

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());

        verify(postRepository, times(1)).deleteById(1);
    }
}
