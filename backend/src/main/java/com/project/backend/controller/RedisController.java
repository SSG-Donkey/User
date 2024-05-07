package com.project.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@Log
@RestController
@RequiredArgsConstructor
public class RedisController {

    @GetMapping("api/session")
    public String getSessionId(HttpSession session) {
        log.info("RedisController 진입");
        session.setAttribute("name", "lee");

        
        String id = session.getId();
        log.info("" + session.getAttribute("name"));
        return id;
    }
}