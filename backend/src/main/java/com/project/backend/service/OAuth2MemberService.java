package com.project.backend.service;

import com.project.backend.entity.User;
import com.project.backend.repository.UserRepository;
import com.project.backend.security.PrincipalDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserRegistrationService userRegistrationService;

    public OAuth2MemberService(UserRepository userRepository, UserRegistrationService userRegistrationService) {
        this.userRepository = userRepository;
        this.userRegistrationService = userRegistrationService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRegistrationService.registerNewUser(attributes));

        // 바로 로그인 처리를 위해 비밀번호 체크 없이 사용자 세부정보 반환
        return new PrincipalDetails(user, attributes);
    }

    @Transactional
    public void updateUserLoginDetails(String email, Map<String, Object> attributes) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRegistrationService.registerNewUser(attributes));

        String newName = (String) attributes.get("name");
        if (newName != null && !newName.equals(user.getNickname())) {
            user.setNickname(newName);
        }

        userRepository.save(user);
    }
}
