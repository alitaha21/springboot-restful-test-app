package com.example.drive.post;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping
    List<Post> findAll() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Post> findById(@PathVariable Integer id) {
        return Optional.ofNullable(postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    Post create(@RequestBody @Valid Post post) {
        return postRepository.save(post);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("/{id}")
    Post update(@PathVariable Integer id, @RequestBody @Valid Post post) {
        Optional<Post> existing =  postRepository.findById(id);
        if (existing.isEmpty()) {
            throw new PostNotFoundException();
        }
        Post updated = new Post(
                existing.get().id(),
                existing.get().userId(),
                post.title(),
                post.body(),
                existing.get().version()
        );
        return postRepository.save(updated);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    void delete(@PathVariable Integer id) {
        postRepository.deleteById(id);
    }

}
