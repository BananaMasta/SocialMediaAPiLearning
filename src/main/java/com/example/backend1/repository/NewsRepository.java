package com.example.backend1.repository;

import com.example.backend1.model.News;
import com.example.backend1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByUser(User user);
}
