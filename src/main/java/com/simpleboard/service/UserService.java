package com.simpleboard.service;

import com.simpleboard.domain.User;
import com.simpleboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입: username 중복 확인 후 암호화된 비밀번호와 nickname을 함께 저장
     */
    public User registerUser(String username, String email, String password, String nickname) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }
        String encodedPwd = passwordEncoder.encode(password);
        User user = new User(username, email, encodedPwd, nickname);
        return userRepository.save(user);
    }

    /**
     * ID로 사용자 조회
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    /**
     * 로그인: username으로 조회 후 암호를 검증
     */
    public User login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
        return user;
    }

    /**
     * 인증용: 아이디/비밀번호 일치시 User 반환, 아니면 null
     */
    public User findByUsernameAndPassword(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPassword()))
                .orElse(null);
    }
}

