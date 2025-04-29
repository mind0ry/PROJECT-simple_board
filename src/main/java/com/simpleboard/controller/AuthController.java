package com.simpleboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // GET /login 요청이 오면 templates/login.html을 렌더링
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
