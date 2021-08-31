package com.example.snslogin.domain;

import lombok.Builder;
import lombok.Data;

@Data
public class User {
    private String id;
    private String email;
    private String username;

    @Builder
    public User(String email, String username) {
        this.email = email;
        this.username = username;
    }
}
