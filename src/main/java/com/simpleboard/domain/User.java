package com.simpleboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity  // JPA가 이 클래스를 테이블로 인식하게 해주는 어노테이션
@Getter   // Lombok: getter 자동 생성
@NoArgsConstructor  // Lombok: 기본 생성자 자동 생성
public class User {

    @Id  // PK (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // auto_increment
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    public User(String username, String email, String password, String nickname) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;

    }
}
