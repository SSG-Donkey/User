package com.project.backend.service;

import com.project.backend.entity.User;
import com.project.backend.jwt.JwtUtil;
import com.project.backend.repository.UserRepository;
import com.project.backend.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        User user = userRepository.findByEmail(email).orElseGet(() -> createUser(email, attributes));

        // 새로운 유저인지 여부 판단
        boolean isNewUser = user.isNewUser();

        // JWT 토큰 생성
        String token = jwtUtil.createToken(user, isNewUser);

        PrincipalDetails principalDetails = new PrincipalDetails(user, attributes);
        principalDetails.setToken(token);

        // 첫 로그인 이후에는 isNewUser를 false로 변경
        if (isNewUser) {
            user.setNewUser(false);
            userRepository.save(user);
        }

        return principalDetails;
    }

    private User createUser(String email, Map<String, Object> attributes) {
        User user = new User(
                (String) attributes.get("name"),
                email,
                "",
                email,
                null,
                null
        );
        userRepository.save(user);
        return user;
    }
}