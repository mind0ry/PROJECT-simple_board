package com.simpleboard.config;

import com.simpleboard.security.JwtAuthFilter;
import com.simpleboard.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (API 사용 시 필요)
                .csrf(csrf -> csrf.disable())

                // 세션 사용 안 함 (JWT Stateless)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL별 인가 설정
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        // 로그인·회원가입 뷰 및 처리
                        .requestMatchers(HttpMethod.GET,  "/login", "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login", "/register").permitAll()

                        // 기존 API 로그인·회원가입
                        .requestMatchers(HttpMethod.POST, "/users", "/users/login").permitAll()

                        // 게시판 열람 (전체 및 상세) 허용
                        .requestMatchers(HttpMethod.GET, "/posts", "/posts/**").permitAll()

                        // 게시글 작성·수정·삭제는 인증 필요
                        .requestMatchers(HttpMethod.POST,   "/posts").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/posts/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/posts/**").authenticated()

                        // 댓글 작성(JSON API) 허용하되 인증 필요
                        .requestMatchers(HttpMethod.POST, "/posts/*/comments").authenticated()

                        // 그 외 모든 요청 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 인증 필터 등록
                .addFilterBefore(
                        new JwtAuthFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
