package com.project.backend.service;

import com.project.backend.dto.KakaoUserDto;
import com.project.backend.dto.LoginRequestDto;
import com.project.backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
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

    // Token 생성
    public KakaoUserDto getUserToken(String code) {

        String host = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();

        // header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        log.info("헤더 : " + headers);

        // body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "bbdfe22726c4cbce4bb5020c4c988a3c");
        body.add("redirect_uri", "https://www.dangnagwi.store/user/kakaoAuth");
        body.add("code", code);
        body.add("client_secret", "6uBd61JFuIXArjlUPDaTdAma8suYgoA1");
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
        log.info("request Status : " + response.getStatusCode());

        return response.getBody();
    }

    // id_token에 있는 UserInfo 가져오기
    public Map<String, Object> getUserInfo(String info) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey("6uBd61JFuIXArjlUPDaTdAma8suYgoA1")
                .build()
                .parseClaimsJws(info)
                .getBody();

        Map<String, Object> userInfo = new HashMap<>();

        userInfo.put("nickname", claims.get("nickname", String.class));
        userInfo.put("email", claims.get("email", String.class));
        return userInfo;
    }
}
