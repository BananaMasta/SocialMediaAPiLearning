package com.example.backend1.controller;

import com.example.backend1.model.Posts;
import com.example.backend1.model.User;
import com.example.backend1.repository.PostRepository;
import com.example.backend1.repository.UserRepository;
import com.example.backend1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostsController {
    @Autowired
    private UserService userService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/usersposts")
    public ResponseEntity<?> createPost(@RequestBody @Validated Posts newPost, Authentication auth) {
        Posts post = new Posts(newPost.getUser(), newPost.getHeader(), newPost.getText());
        post.setUser(userService.getUserByToken((Jwt) auth.getCredentials()));
        postRepository.save(post);
        return ResponseEntity.ok("Post created successfully");
    }

    @PostMapping("/deletepost")
    public ResponseEntity<HttpStatus> deletePost(@RequestParam @Validated String postId, Authentication auth) {
        Posts posts = postRepository.findPostsById(Long.valueOf(postId));
        User user = userService.getUserByToken((Jwt) auth.getCredentials());
        if (postRepository.existsByUser(user)) {
            postRepository.delete(posts);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<HttpStatus> updatePost(@RequestParam @Validated String postId, Authentication auth, String header, @RequestBody Posts newPost) {
        User user = userService.getUserByToken((Jwt) auth.getCredentials());
        if (!userRepository.existsByUsername(user.getUsername())) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        if (!postRepository.existsByUser(user)) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        if (!postRepository.existsByHeader(header)) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        if (postRepository.existsById(Long.valueOf(postId))) {
            Posts posts = postRepository.findPostsById(Long.valueOf(postId));
            System.out.println(posts.getText());
        }
        Posts posts = postRepository.findPostsById(Long.valueOf(postId));
        posts.setHeader(newPost.getHeader());
        posts.setText(newPost.getText());
        postRepository.save(posts);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

