package com.simpleboard.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final String username;

    public JwtAuthentication(String username) {
        super(AuthorityUtils.NO_AUTHORITIES); // 권한은 지금은 없음
        this.username = username;
        setAuthenticated(true); // 인증된 것으로 설정
    }

    @Override
    public Object getCredentials() {
        return null; // 패스워드는 필요 없음
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}
