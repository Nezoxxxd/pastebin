package com.project.pastebin.services;

import com.project.pastebin.entities.Post;
import com.project.pastebin.entities.User;
import com.project.pastebin.exceptions.PostNotFoundException;
import com.project.pastebin.repositories.PostRepository;
import com.project.pastebin.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.AccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Post getPost(String email, Long postId) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("This user doesn't have post with this id " + postId)
        );

        if(!post.getUser().getEmail().equals(user.getEmail())) {
            try {
                throw new AccessException("User with the email " + user.getEmail() + " does not belong to this post");
            } catch (AccessException e) {
                throw new RuntimeException(e);
            }
        }

        return post;
    }

    public List<Post> getAllPosts(String email) {
        if(email == null) {
            return postRepository.findAll();
        }

        User user = userRepository.findUserByEmail(email).orElseThrow(
                () ->  new UsernameNotFoundException("User with email " + email + " not found")
        );

        return postRepository.findAllPostsByUserId(user.getId()).orElseThrow(
                () -> new PostNotFoundException("This user does not have any posts")
        );
    }

    public Post createPost(Post userPost, Long lifetimeHours) {
        Post post;
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(userDetails != null) {
            User user = (User) userDetails;

            post = Post.builder()
                    .postTitle(userPost.getPostTitle())
                    .postText(userPost.getPostText())
                    .user(user)
                    .build();

            if(lifetimeHours != null) {
                post.setExpiryTime(LocalDateTime.now().plusHours(lifetimeHours));
            } else {
                post.setExpiryTime(null);
            }
        } else {
            throw new UsernameNotFoundException("User not authenticate");
        }

        return postRepository.save(post);
    }

    public Post updatePost(Post userPost, Long postId) {
        Post updatePost = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("Post not found")
        );

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail = userDetails.getUsername();

        if(userEmail != null && userEmail.equals(updatePost.getUser().getEmail())) {
            updatePost.setPostTitle(userPost.getPostTitle());
            updatePost.setPostText(userPost.getPostText());
        }

        postRepository.save(updatePost);
        return updatePost;
    }

    public void deletePost(Long postId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails == null) {
            throw new UsernameNotFoundException("User not authenticate");
        }

        String userEmail = userDetails.getUsername();
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("Post not found")
        );

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin || userEmail.equals(post.getUser().getEmail())) {
            postRepository.delete(post);
        } else {
            try {
                throw new AccessException("You do not have permission to delete this post");
            } catch (AccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
