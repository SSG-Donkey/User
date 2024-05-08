package com.project.backend.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.project.backend.dto.TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.entity.UserRoleEnum;
import com.project.backend.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Log
@RestController
@RequiredArgsConstructor
public class RedisController {
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("api/session")
    public String getSessionId(HttpSession session, HttpServletResponse response) throws UnsupportedEncodingException {
        log.info("RedisController 진입");
        session.setAttribute("name", "lee");
        String name = (String) session.getAttribute("name");
        redisTemplate.opsForValue().set("name", name);
        TokenDto token = jwtUtil.createToken(name, UserRoleEnum.ADMIN);

        Cookie cookie = new Cookie("accesstoken", URLEncoder.encode(token.getAccessToken(), "EUC-KR"));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        return token.getAccessToken();
    }

    @GetMapping("api/auth")
    public String authTest() {
        log.info("AccessToken 인증");


        return null;
    }
}