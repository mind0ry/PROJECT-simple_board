package com.simpleboard.controller;

import com.simpleboard.domain.User;
import com.simpleboard.service.UserService;
import com.simpleboard.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 로그인 폼 표시
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "로그인에 실패했습니다.");
        }
        return "login";
    }

    /**
     * 로그인 처리 (폼 제출)
     */
    @PostMapping("/login")
    public String loginSubmit(@RequestParam String username,
                              @RequestParam String password,
                              HttpServletResponse response,
                              Model model) {
        User user = userService.findByUsernameAndPassword(username, password);
        if (user == null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "login";
        }
        // JWT 생성 후 쿠키에 저장
        String token = jwtUtil.generateToken(user.getUsername());
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        // 로그인 성공 시 게시판 목록으로 이동
        return "redirect:/posts";
    }

    /**
     * 회원가입 폼 표시
     */
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/register")
    public String registerSubmit(@RequestParam String username,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String nickname,
                                 Model model) {
        try {
            userService.registerUser(username, email, password, nickname);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
