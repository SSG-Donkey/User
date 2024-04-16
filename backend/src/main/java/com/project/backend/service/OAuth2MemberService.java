package com.project.backend.service;

import com.project.backend.entity.User;
import com.project.backend.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    // UserRepository 주입
    public OAuth2MemberService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        // 사용자 정보를 처리하고, 필요한 경우 추가적인 작업을 수행합니다.
        // 예를 들어, PrincipalDetails 객체를 생성하여 반환할 수 있습니다.

        return oAuth2User;
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

}
