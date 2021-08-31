package com.example.snslogin.service;

import com.example.snslogin.mapper.UserMapper;
import com.example.snslogin.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public void join(User user){
        userMapper.joinUser(user);
    }

    public User searchUser(String username){
        return userMapper.getUserByUsername(username);
    }

}
