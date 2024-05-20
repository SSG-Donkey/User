package com.project.backend.service;

import com.project.backend.entity.User;
import com.project.backend.repository.UserRepository;
import com.project.backend.security.PrincipalDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserService userService; // UserService 추가

    // 수동으로 생성자 작성
    public OAuth2MemberService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");

        userService.updateUserLoginDetails(email, attributes);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        return new PrincipalDetails(user, attributes);
    }
    private User registerNewUser(Map<String, Object> attributes) {
        User newUser = new User(
                (String) attributes.get("name"),
                (String) attributes.get("email"),
                "", // Password
                (String) attributes.get("email"),
                null, // Bank number
                null  // Account number
        );
        return userRepository.save(newUser);
    }

    private User updateExistingUser(User user, Map<String, Object> attributes) {
        user.setNickname((String) attributes.get("name"));
        return userRepository.save(user);
    }
}
