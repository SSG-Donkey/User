package com.project.backend.service;

import com.project.backend.entity.User;
import com.project.backend.repository.UserRegistrationService;
import com.project.backend.repository.UserRepository;
import com.project.backend.security.PrincipalDetails;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    private final UserRegistrationService userRegistrationService; // 인터페이스 사용

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

        return new PrincipalDetails(user, attributes);
    }
}

