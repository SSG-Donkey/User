package com.project.backend.service;

import com.project.backend.dto.GoogleMemberInfo;
import com.project.backend.dto.KakaoMemberInfo;
import com.project.backend.dto.OAuth2MemberInfo;
import com.project.backend.entity.User;
import com.project.backend.jwt.JwtUtil;
import com.project.backend.repository.UserRepository;
import com.project.backend.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import lombok.extern.java.Log;

import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2MemberInfo memberInfo = null;

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String name = null;

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if (registrationId.equals("google")) {
            log.info("인증수단 : google ");
            memberInfo = new GoogleMemberInfo(oAuth2User.getAttributes());

        } else if (registrationId.equals("kakao")) {
            log.info("인증수단 : kakao ");
            memberInfo = new KakaoMemberInfo(oAuth2User.getAttributes());
        }

        String email = memberInfo.getEmail();

        log.info("사용자 정보 : " + memberInfo.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, attributes));

        // JWT 토큰 생성
        String token = jwtUtil.createToken(user);

        PrincipalDetails principalDetails = new PrincipalDetails(user, attributes);
        principalDetails.setToken(token);
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