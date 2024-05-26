package com.project.pastebin.controllers;

import com.project.pastebin.entities.Post;
import com.project.pastebin.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/user/posts")
    public List<Post> getAllUsersPosts(@RequestParam(required = false) String email) {
        return postService.getAllPosts(email);
    }

    @GetMapping("/user/{email}/post")
    public Post getUsersPost(@PathVariable String email, @RequestParam Long postId) {
        return postService.getPost(email, postId);
    }

    @PostMapping("/user/create-post")
    public Post createPost(@RequestBody Post post, @RequestParam(required = false) Long lifetimeHours) {
        return postService.createPost(post, lifetimeHours);
    }

    @PutMapping("/user/update-post")
    public Post updatePost(@RequestBody Post post, @RequestParam Long postId) {
        return postService.updatePost(post, postId);
    }

    @DeleteMapping("/user/delete-post")
    public String deletePost(@RequestParam Long postId) {
        postService.deletePost(postId);
        return "Post deleted successfully";
    }
}
