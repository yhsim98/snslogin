package com.example.snslogin.controller;

import com.example.snslogin.service.CustomOAuth2UserService;
import com.example.snslogin.service.UserService;
import com.example.snslogin.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final CustomOAuth2UserService customOAuth2UserService;

    @PostMapping("/api/v1/users")
    public ResponseEntity<String> UserJoin(@RequestBody User user){
        userService.join(user);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    // uri : {REDIRECT_URI}?code={AUTHORIZE_CODE}
    // 여기서는 /oauth2/authorization/kakao?code={AUTHORIZE_CODE}
    // 리다이렉트 uri 이다. kakao developers 에서 사전에 설정한 url 의 파라미터에 인증 code 를 붙여서 보내준다
    @GetMapping("/oauth2/authorization/kakao")
    public ResponseEntity<String> kakaoCallBack(String code){
        customOAuth2UserService.kakaoCallBack(code);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
