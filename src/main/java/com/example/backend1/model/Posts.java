package com.example.backend1.model;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    private User user;
    private String header;
    private String text;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Posts() {

    }

    public Posts(User user, String header, String text) {
        this.user = user;
        this.header = header;
        this.text = text;
    }
}
