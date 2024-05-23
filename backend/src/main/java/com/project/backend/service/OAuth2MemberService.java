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
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, attributes));

        String token = jwtUtil.createToken(user.getEmail(), user.getRole(), user.getId(), user.getNickname(), user.getEmail(), user.getBankNo(), user.getAccount());

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