package com.project.backend.security;

import com.project.backend.entity.User;
import com.project.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자를 찾는 로직. 여기서는 email을 username으로 사용한다고 가정.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // PrincipalDetails 객체를 생성하여 반환. 여기서는 OAuth2 속성은 사용하지 않으므로 첫 번째 생성자를 사용.
        return new PrincipalDetails(user);
    }
}
