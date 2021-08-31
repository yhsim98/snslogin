package com.example.snslogin.mapper;

import com.example.snslogin.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    User getUserByUsername(String username);
    void joinUser(User user);
}
