package com.example.backend1.repository;

import com.example.backend1.model.Posts;
import com.example.backend1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Posts, Long> {
    boolean existsByUser(User user);

    List<Posts> findAllByUser(User user);

    Posts findPostsByUser(User user);

    Posts findPostsById(Long id);

    boolean existsByHeader(String header);

}
