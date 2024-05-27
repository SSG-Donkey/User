package com.project.backend.config;

import com.project.backend.jwt.JwtAuthFilter;
import com.project.backend.security.PrincipalDetails;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity // Spring Security 설정을 활성화
@EnableGlobalMethodSecurity(securedEnabled = true) // @Secured 어노테이션을 사용하여 메서드 수준의 보안 설정 활성화
@RequiredArgsConstructor // Lombok을 사용하여 final 필드에 대한 생성자를 자동으로 생성
public class WebSecurityConfig {

    private final OAuth2MemberService oAuth2MemberService; // OAuth2 로그인 서비스
    private final JwtAuthFilter jwtAuthFilter; // JWT 인증 필터

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화를 위한 PasswordEncoder 빈 설정
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 정적 자원에 대한 보안 설정을 무시
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://www.dangnagwi.store")); // 허용할 출처 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드 설정
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token")); // 허용할 헤더 설정
        configuration.setAllowCredentials(true); // 자격 증명을 허용
        configuration.setMaxAge(3600L); // preflight 요청 캐시 시간 설정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정 적용
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
                .csrf().disable() // CSRF 보호 비활성화
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 정책을 STATELESS로 설정하여 세션을 사용하지 않음
                .and().authorizeRequests() // 요청에 대한 보안 설정 시작
                .antMatchers("/**").permitAll() // 모든 경로에 대한 접근을 허용
                .and().oauth2Login() // OAuth2 로그인 설정
                .loginPage("https://www.dangnagwi.store/loginForm.html") // 로그인이 필요한 경우 이동할 로그인 페이지 설정
                .successHandler((request, response, authentication) -> { // 로그인 성공 후 처리 설정
                    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
                    String token = principalDetails.getToken(); // 인증된 사용자의 토큰 가져오기

                    String redirectUrl = "https://www.dangnagwi.store/loginForm.html?token=" + token; // 로그인 성공 후 리디렉션할 URL 설정
                    response.sendRedirect(redirectUrl); // 리디렉션 수행
                })
                .userInfoEndpoint().userService(oAuth2MemberService) // OAuth2 로그인 후 사용자 정보 가져오기
                .and()
                .and().addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가

        return http.build(); // 설정된 SecurityFilterChain 빌드 및 반환
    }
}
