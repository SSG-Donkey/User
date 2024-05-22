package com.project.backend.security;

import com.project.backend.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;


import java.util.Collection;
import java.util.Collections;
import java.util.Map;


@Getter
@Setter
public class PrincipalDetails implements OAuth2User, UserDetails {

    private User user;
    private Map<String, Object> attributes;
    private String token;
    private String email;
    private String username;
    private String nickname;
    private Long userId;
    private Long account;
    private Long bankNo;




    public PrincipalDetails(User user) {
        this.user = user;
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.username = user.getUsername();
        this.account = user.getAccount();
        this.bankNo = user.getBankNo();

    }

    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.username = user.getUsername();
        this.account = user.getAccount();
        this.bankNo = user.getBankNo();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

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
        return user.getId().toString();
    }
}