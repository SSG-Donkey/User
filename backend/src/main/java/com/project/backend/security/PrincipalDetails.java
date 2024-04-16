package com.project.backend.security;

import com.project.backend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class PrincipalDetails implements OAuth2User, UserDetails {

    private User user;
    private Map<String, Object> attributes;

    // 기존 생성자
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // OAuth2 로그인을 위한 생성자 오버로딩
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // OAuth2에서는 일반적으로 비밀번호를 사용하지 않으므로 null 반환 가능
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // 계정이 만료되지 않았는지 반환
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨있지 않은지 반환
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 자격증명이 만료되지 않았는지 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화(사용가능) 상태인지 반환
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getId().toString(); // 고유 식별자 반환
    }
}