package com.simpleboard.service;

import com.simpleboard.domain.User;
import com.simpleboard.repository.UserRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service  // 이 클래스가 서비스 역할임을 Spring에 알려줌 (Bean 등록)
@RequiredArgsConstructor  // 생성자 자동 생성 (final 필드 주입용)
public class UserService {

    private final UserRepository userRepository;

    public User registerUser(String username, String email, String password) {
        // 1. 유저 중복 여부 확인 (예: username이 이미 존재하는지)
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        // 2. 유저 저장
        User user = new User(username, email, password);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        return user;
    }

}
