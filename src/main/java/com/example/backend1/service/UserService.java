package com.example.backend1.service;

import com.example.backend1.model.User;
import com.example.backend1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUserByToken(Jwt token) {
        return userRepository.findByUsername((String) (token.getClaims().get("sub")));
    }

}
