//package com.project.backend.utils;
//
//import com.project.backend.entity.UserRoleEnum;
//import com.project.backend.jwt.JwtUtil;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    private JwtUtil jwtUtil;
//
//    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        String email = ((OAuth2User) authentication.getPrincipal()).getAttribute("email");
//        UserRoleEnum role = UserRoleEnum.USER; // 사용자 역할 설정
//        String token = jwtUtil.createToken(email, role).getAccessToken();
//
//        response.addHeader("Authorization", "Bearer " + token);
//        response.sendRedirect("https://www.dangnagwi.store/index.html"); // 리다이렉트 주소 설정
//    }
//}
