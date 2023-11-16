package com.example.backend1.controller;

import com.example.backend1.model.*;
import com.example.backend1.repository.NewsRepository;
import com.example.backend1.repository.MessageRepository;
import com.example.backend1.repository.UserRepository;
import com.example.backend1.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    NewsRepository newsRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username \"" + user.getUsername() + "\" is taken");
        }
        user.setRoles(new HashSet<>(List.of(Role.ROLE_USER)));
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = userRepository.save(user);
        return new ResponseEntity<>(newUser.getId(), HttpStatus.OK);
    }

    @GetMapping("/personal/info")
    @JsonView(Views.UserPersonalInformation.class)
    public ResponseEntity<?> getPersonalInfo(Authentication auth) {
        User user = userService.getUserByToken((Jwt) auth.getCredentials());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/addfriend")
    public ResponseEntity<?> addFriendRequest(@Validated @RequestParam String userName, Authentication auth) {
        if (!userRepository.existsByUsername(userName)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = userService.getUserByToken((Jwt) auth.getCredentials());
        User user2 = userRepository.findByUsername(userName);
        List<User> waiting = user2.getWaitingRequestList();
        List<User> subscriber = user2.getSubscriberList();
        waiting.add(user);
        subscriber.add(user);
        user2.setSubscriberList(subscriber);
        userRepository.save(user2);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/acceptfriend")
    public ResponseEntity<HttpStatus> acceptFriendRequest(@Validated @RequestParam String userName, Authentication auth, @RequestBody String acceptRequest) {
        User user = userService.getUserByToken((Jwt) auth.getCredentials());
        User user2 = userRepository.findByUsername(userName);
        List<User> waiting = user.getWaitingRequestList();
        List<User> friend = user.getFriendList();
        List<User> friend2 = user2.getFriendList();
        List<User> subscriber = user2.getSubscriberList();
        if (acceptRequest.contains("accept")) {
            waiting.remove(user2);
            subscriber.add(user);
            friend.add(user2);
            friend2.add(user);
            userRepository.save(user);
        } else if (acceptRequest.contains("reject")) {
            waiting.remove(user2);
            userRepository.save(user);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/deletefriend")
    public ResponseEntity<HttpStatus> deleteFriend(@Validated @RequestParam String userName, Authentication auth, @RequestBody String delete) {
        User user = userService.getUserByToken((Jwt) auth.getCredentials());
        User user2 = userRepository.findByUsername(userName);
        List<User> friend = user.getFriendList();
        List<User> friend2 = user2.getFriendList();
        List<User> subscriber = user2.getSubscriberList();
        if (delete.contains("delete")) {
            subscriber.remove(user);
            friend2.remove(user);
            friend.remove(user2);
            userRepository.save(user);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/message")
    public ResponseEntity<HttpStatus> userDialog(@Validated @RequestParam String userId, Authentication auth, @RequestBody String message) {
        User user = userService.getUserByToken((Jwt) auth.getCredentials());
        User user1 = userRepository.findById(Long.parseLong(userId));
        List<User> friend = user.getFriendList();
        if (friend.contains(user1)) {
            Message userMessage = new Message(user, user1, message);
            messageRepository.save(userMessage);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/newscreate")
    public ResponseEntity<HttpStatus> userFeedCreate(@Validated Authentication auth, @RequestBody String text) {
        User user = userService.getUserByToken((Jwt) auth.getCredentials());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        News news = new News(user, text, timestamp);
        newsRepository.save(news);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/news")
    public ResponseEntity<?> userFeedList(@Validated Authentication auth) {
        User user = userService.getUserByToken((Jwt) auth.getCredentials());
        List<User> subserList = user.getSubscriberList();
        List<News> news = new ArrayList<>();
        for (int i = 0; i < subserList.size(); i++) {
            List<News> newsList = newsRepository.findByUser(subserList.get(i));
            news.addAll(newsList);
        }
        return new ResponseEntity<>(news, HttpStatus.OK);
    }
}
