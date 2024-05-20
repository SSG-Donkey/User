package com.project.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class OAuthController {
    @GetMapping("/loginForm")
    public String home() {
        return "loginForm";
    }

    @GetMapping("/private")
    public String privatePage() {
        return "privatePage";
    }
    @GetMapping("/admin")
    public String adminPage() {
        return "adminPage";
    }
}
 