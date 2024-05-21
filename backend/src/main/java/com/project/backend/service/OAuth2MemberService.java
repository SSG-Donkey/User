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

    // UserRepository 주입
    public OAuth2MemberService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // OAuth2 제공자로부터 받은 사용자 정보
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println("oAuth2User = " + attributes);

        // 사용자의 이메일을 기준으로 사용자를 찾거나 새로운 사용자를 생성합니다.
        String email = (String) attributes.get("email");
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, attributes));

        // JWT 토큰 생성
        String token = jwtUtil.createToken(user.getUsername(), user.getRole());

        // PrincipalDetails 객체를 생성하여 반환합니다.
        return new PrincipalDetails(user, attributes, token);
    }

    private User createUser(String email, Map<String, Object> attributes) {
        // 새 사용자 생성 로직
        User user = new User(
                (String) attributes.get("name"), // nickname
                email, // username, 이 예제에서는 email을 username으로 사용
                "", // password, OAuth2 인증에서는 비밀번호가 필요 없으므로 빈 문자열 처리
                email, // email
                null, // bankNo, NULL로 설정
                null  // account, NULL로 설정
        );
        userRepository.save(user);
        return user;
    }

    // PrincipalDetails에 토큰을 포함한 커스텀 클래스 생성
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
