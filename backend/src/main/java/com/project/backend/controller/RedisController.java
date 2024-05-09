package com.project.backend.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.project.backend.dto.TokenDto;
import com.project.backend.jwt.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.entity.UserRoleEnum;
import com.project.backend.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

@Log
@RestController
@RequiredArgsConstructor
public class RedisController {
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private JwtAuthFilter jwtAuthFilter;

    @GetMapping("api/session")
    public String getSessionId(HttpSession session, HttpServletResponse response) throws UnsupportedEncodingException {
        log.info("RedisController 진입");
        session.setAttribute("name", "kyeong");
        String name = (String) session.getAttribute("name");
        redisTemplate.opsForValue().set("name", name);

        TokenDto token = jwtUtil.createToken(name, UserRoleEnum.ADMIN);

        Cookie cookie = new Cookie("accessToken", URLEncoder.encode(token.getAccessToken(), "EUC-KR"));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        return token.getAccessToken();
    }

    @GetMapping("api/auth")
    public void authTest(HttpSession session, @CookieValue("accessToken") String accessToken) {
        log.info("AccessToken Auth");
        String decode = URLDecoder.decode(accessToken, StandardCharsets.UTF_8);
        log.info("AccessToken: " + decode);
        String name = jwtUtil.getUserInfoFromToken(decode.substring(7));
        log.info("name : " + name);

        jwtUtil.createAuthentication(name);
    }

    @GetMapping("api/time")
    public long auth(@CookieValue("accessToken") String accessToken) {
        log.info("AccessToken Auth");
        String decode = URLDecoder.decode(accessToken, StandardCharsets.UTF_8).substring(7);

        log.info("AccessToken value : " + decode);
        return jwtUtil.getRemainingTime(decode);
    }
}
