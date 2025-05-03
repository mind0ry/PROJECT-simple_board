package com.simpleboard.controller;

import com.simpleboard.domain.User;
import com.simpleboard.service.UserService;
import com.simpleboard.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 회원가입
    @PostMapping
    public ResponseEntity<User> register(@RequestBody UserRegisterRequest request) {
        User created = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getNickname()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 특정 유저 조회
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return ResponseEntity.ok(user);
    }

    // 로그인 → 쿠키에 JWT, 닉네임 쿠키에 담아서 반환
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        User u = userService.findByUsernameAndPassword(
                req.getUsername(), req.getPassword()
        );
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 실패");
        }

        // 1) JWT 토큰 발급
        String token = jwtUtil.generateToken(u.getUsername());

        // 2) ACCESS_TOKEN 쿠키 설정 (HttpOnly)
        ResponseCookie tokenCookie = ResponseCookie.from("ACCESS_TOKEN", token)
                .httpOnly(true)
                .path("/")
                .maxAge(24 * 60 * 60)    // 1일
                .sameSite("Lax")
                .build();

        // 3) NICKNAME 쿠키 설정 (JS 접근 가능)
        ResponseCookie nickCookie = ResponseCookie.from("NICKNAME", u.getNickname())
                .httpOnly(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, nickCookie.toString())
                .body(new TokenResponse(token, u.getNickname()));
    }

    // --- DTO 클래스들 ---

    public static class UserRegisterRequest {
        private String username;
        private String email;
        private String password;
        private String nickname;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class TokenResponse {
        private String token;
        private String nickname;

        public TokenResponse(String token, String nickname) {
            this.token = token;
            this.nickname = nickname;
        }

        public String getToken() { return token; }
        public String getNickname() { return nickname; }
    }
}