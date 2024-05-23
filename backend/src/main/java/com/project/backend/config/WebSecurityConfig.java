package com.project.backend.config;

import com.project.backend.jwt.JwtAuthFilter;
import com.project.backend.jwt.JwtUtil;
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
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final OAuth2MemberService oAuth2MemberService;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtUtil jwtUtil;  // 추가

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://www.dangnagwi.store"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers("/**").permitAll()
                .and().oauth2Login()
                .loginPage("https://www.dangnagwi.store/loginForm.html")
                .successHandler((request, response, authentication) -> {
                    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
                    // JWT 토큰 생성 및 포함할 사용자 정보
                    String token = jwtUtil.createToken(
                            principalDetails.getUsername(),
                            principalDetails.getUser().getRole(),
                            principalDetails.getUser().getId(),
                            principalDetails.getUser().getNickname(),
                            principalDetails.getUser().getEmail(),
                            principalDetails.getUser().getBankNo(),
                            principalDetails.getUser().getAccount()
                    );
                    String redirectUrl = "https://www.dangnagwi.store/mypage.html?token=" + token;
                    response.sendRedirect(redirectUrl);
                })
                .userInfoEndpoint().userService(oAuth2MemberService)
                .and()
                .and().addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}