package com.simpleboard.controller;

import com.simpleboard.domain.User;
import com.simpleboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users") // 모든 URL이 /users로 시작함
public class UserController {

    private final UserService userService;

    @PostMapping
    public User register(@RequestBody UserRegisterRequest request) {
        return userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }


    // 내부 클래스: 요청 바디용 DTO
    public static class UserRegisterRequest {
        private String username;
        private String email;
        private String password;

        // getter, setter 필수 (Lombok 안 써도 되지만 써도 OK)
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {
        System.out.println("🔥 로그인 컨트롤러 진입!");

        System.out.println("요청 들어온 username: " + request.getUsername());
        System.out.println("요청 들어온 password: " + request.getPassword());

        return userService.login(request.getUsername(), request.getPassword());
    }


    // 내부 클래스 (DTO)
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
