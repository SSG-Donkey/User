package com.project.backend.service;

import com.project.backend.entity.User;
import com.project.backend.entity.UserRoleEnum;
import com.project.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerNewUser(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        User newUser = new User(name, email, passwordEncoder.encode("12345"), email, null, null);
        newUser.setRole(UserRoleEnum.USER);
        return userRepository.save(newUser);
    }
}
