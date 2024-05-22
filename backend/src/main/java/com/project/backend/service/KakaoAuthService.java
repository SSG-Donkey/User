package com.project.backend.service;

import com.project.backend.dto.KakaoUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Log
public class KakaoAuthService {
    private KakaoUserDto kakaoUserDto;

    private static final String CLIENT_ID = "bbdfe22726c4cbce4bb5020c4c988a3c";
    private static final String REDIRECT_URI = "http://localhost:8080/user/kakaoAuth";

    //인가코드 받기
    public String getAuth() {
        RestTemplate restTemplate = new RestTemplate();
        log.info("kakaoAuth 진입");

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("response_type", "code");
        String url = builder.build().toString();
        String auth = restTemplate.getForObject(url, String.class);

        log.info("auth 값 : " + auth);

        return auth;
    }

    // Token 및 UserInfo가져오기
    public KakaoUserDto getUserInfo(String code) {
        log.info("getUserInfo의 code: " + code);
        String host = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();

        // header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        log.info("헤더 : " + headers);

        // body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", CLIENT_ID);
        body.add("redirect_uri", "http://localhost:8080/user/kakaoAuth");
        body.add("code", code);
        log.info("바디 : " + body);

        // header + body
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        log.info("user Request: " + kakaoTokenRequest);

        ResponseEntity<KakaoUserDto> response = restTemplate.exchange(
                host,
                HttpMethod.POST,
                kakaoTokenRequest,
                KakaoUserDto.class
        );

        return response.getBody();
    }
}
