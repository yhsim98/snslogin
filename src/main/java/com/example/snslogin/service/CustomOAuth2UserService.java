package com.example.snslogin.service;

import com.example.snslogin.config.auth.dto.KakaoOauth2Token;
import com.example.snslogin.domain.KakaoProfile;
import com.example.snslogin.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService {

    @Value("${kakao.restkey}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${kakao.profile-uri}")
    private String kakaoProfileUri;

    private final UserService userService;


    public void kakaoCallBack(String code){
        KakaoOauth2Token kakaoOauth2Token = getOauthToken(code);
        User kakaoUser = getUserInfo(kakaoOauth2Token);

        // 가입자 혹은 비가입자 체크 해서 처리
        User originUser = userService.searchUser(kakaoUser.getUsername());

        //기존회원이 아닌 경우 회원가입
        if(originUser == null) {
            userService.join(kakaoUser);
        }
    }

    // 카카오에서 인가 코드를 받아오는 메서드
    private KakaoOauth2Token getOauthToken(String code){
        // 서버에서 RESTful 한 요청을 보낼 때 사용한느 web 요청 인터페이스이다
        // webclient 를 사용하는 것을 권장한다. resttamplate은 이제 deprecated 되어 쓰이지 않는다...
        RestTemplate restTemplate = new RestTemplate();

        // 인가 코드를 받아오기 위해 필요한 http header와 body 설정이다.
        // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoRestApiKey);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // resttemplate을 통해 post요청을 보낸다
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // 받아온 http response 의 body를 객체로 매핑해준다
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoOauth2Token oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(response.getBody(), KakaoOauth2Token.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oauthToken;
    }

    // kakao에 rest 요청을 보내 유저의 정보를 받아온다. access token 이 필요하다.
    // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
    private User getUserInfo(KakaoOauth2Token kakaoOauth2Token){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+kakaoOauth2Token.getAccess_token());
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoProfileUri,
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );
        System.out.println(response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(response.getBody(), KakaoProfile.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return User.builder()
                .username(kakaoProfile.getKakao_account().getEmail()+"_"+kakaoProfile.getId())
                .email(kakaoProfile.getKakao_account().getEmail())
                .build();
    }

}
