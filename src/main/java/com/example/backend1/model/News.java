package com.example.backend1.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    private User user;
    private String text;
    private Timestamp newsCreateTime;

    public News() {
    }

    public News(User user, String text, Timestamp newsCreateTime) {
        this.user = user;
        this.text = text;
        this.newsCreateTime = newsCreateTime;
    }
}
