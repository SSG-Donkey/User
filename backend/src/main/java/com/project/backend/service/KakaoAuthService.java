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

    // Token 및 UserInfo가져오기
    public KakaoUserDto getUserInfo(String code) {

        String host = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();

        // header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        log.info("헤더 : " + headers);

        // body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "bbdfe22726c4cbce4bb5020c4c988a3cstat");
        body.add("redirect_uri", "https://www.dangnagwi.store/user/kakaoLogin");
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
