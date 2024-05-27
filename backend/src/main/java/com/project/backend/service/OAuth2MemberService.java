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

    private final UserRepository userRepository; // UserRepository 의존성 주입
    private final JwtUtil jwtUtil; // JwtUtil 의존성 주입

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest); // 부모 클래스의 loadUser 메서드 호출하여 OAuth2User 가져옴
        OAuth2MemberInfo memberInfo = null;

        Map<String, Object> attributes = oAuth2User.getAttributes(); // OAuth2User의 속성 가져오기
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 클라이언트 등록 ID 가져오기
        if (registrationId.equals("google")) { // 구글 로그인인지 확인
            memberInfo = new GoogleMemberInfo(oAuth2User.getAttributes()); // 구글 사용자 정보 매핑

        } else if (registrationId.equals("kakao")) { // 카카오 로그인인지 확인
            memberInfo = new KakaoMemberInfo(oAuth2User.getAttributes()); // 카카오 사용자 정보 매핑
        }

        String email = memberInfo.getEmail(); // 사용자 이메일 가져오기
        String name = memberInfo.getName(); // 사용자 이름 가져오기
        // 이메일을 통해 사용자 조회, 없으면 새로 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, name));

        // JWT 토큰 생성
        String token = jwtUtil.createToken(user);

        // 사용자 정보와 토큰을 PrincipalDetails에 설정
        PrincipalDetails principalDetails = new PrincipalDetails(user, attributes);
        principalDetails.setToken(token);
        return principalDetails;
    }

    // 새로운 사용자를 생성하는 메서드
    private User createUser(String email, String name) {
        User user = new User(
                name, // 사용자 이름
                email, // 사용자 이메일을 username으로 설정
                "", // 비밀번호는 빈 문자열로 설정
                email, // 이메일
                null, // 은행 번호 초기값 null
                null // 계좌 번호 초기값 null
        );
        userRepository.save(user); // 새 사용자 저장
        return user;
    }
}