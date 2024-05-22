package com.project.backend.service;

import com.project.backend.entity.User;
import com.project.backend.jwt.JwtUtil;
import com.project.backend.repository.UserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Service
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public OAuth2MemberService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        System.out.println("OAuth2 Login Attempt: Email=" + email); // 로그인 시도 로그

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, attributes));

        String token = jwtUtil.createToken(user.getUsername(), user.getRole());
        System.out.println("Generated Token: " + token); // 토큰 생성 로그

        return new PrincipalDetails(user, attributes, token);
    }

    private User createUser(String email, Map<String, Object> attributes) {
        User user = new User(
                (String) attributes.get("email"),
                email,
                "",
                email,
                null,
                null
        );
        userRepository.save(user);
        return user;
    }

    public static class PrincipalDetails implements OAuth2User, UserDetails {
        private final User user;
        private final Map<String, Object> attributes;
        private final String token;

        public PrincipalDetails(User user, Map<String, Object> attributes, String token) {
            this.user = user;
            this.attributes = attributes;
            this.token = token;
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

        public String getToken() {
            return token;
        }
    }
}