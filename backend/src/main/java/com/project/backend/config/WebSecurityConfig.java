package com.project.backend.config;

import com.project.backend.jwt.JwtAuthFilter;
import com.project.backend.service.OAuth2MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;


@Configuration
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true) // @Secured 어노테이션 활성화
@RequiredArgsConstructor // Lombok을 사용하여 생성자 주입을 자동으로 처리
public class WebSecurityConfig {
    private final OAuth2MemberService oAuth2MemberService;
    private final JwtAuthFilter jwtAuthFilter; // JwtAuthFilter 주입 받기

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // h2-console 사용 및 resources 접근 허용 설정
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable();

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers("/**").permitAll() // 모든 요청에 대해 접근을 허용
                .antMatchers("/private/**").authenticated() // "/private/**" 경로는 인증 필요
                .and().oauth2Login()
                .loginPage("/loginForm") // 로그인 필요 시 이동할 페이지 지정
                .defaultSuccessUrl("/") // OAuth 로그인 성공 후 리다이렉트 될 기본 URL
                .userInfoEndpoint().userService(oAuth2MemberService) // OAuth 로그인 후 사용자 정보를 처리할 서비스 지정
                .and()
                .and().addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}