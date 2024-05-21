package com.project.backend.service;

import com.project.backend.entity.User;
import com.project.backend.entity.UserRoleEnum;
import com.project.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final UserRepository userRepository;


    @Override
    @Transactional
    public User registerNewUser(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        // 소셜 로그인으로 가입할 때는 비밀번호를 저장하지 않음
        User newUser = new User(name, email, null, email, null, null);
        newUser.setRole(UserRoleEnum.USER);
        return userRepository.save(newUser);
    }
}
